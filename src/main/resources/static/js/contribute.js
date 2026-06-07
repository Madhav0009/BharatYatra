var BY = window.BY || (window.BY = {});

BY.renderContributeForms = function (place) {
  const mount = document.getElementById("contributeMount");
  if (!mount) return;
  mount.className = "";
  mount.innerHTML = `
    <div class="section-head"><div><div class="eyebrow">Selected destination</div><h2>${BY.escapeHtml(place.name)}</h2><p>${BY.escapeHtml(place.state || "")} · ${BY.escapeHtml(place.tagline || "")}</p></div><a class="ghost-button" href="place.html?id=${encodeURIComponent(place.id)}">View guide</a></div>
    ${BY.state.user ? "" : `<div class="notice">Login before submitting a review, story, or media.</div>`}
    <div class="grid cards-3" style="margin-top:14px">
      <form class="card form-grid" id="reviewForm"><h3>Write a review</h3><div class="field"><label for="reviewRating">Rating</label><select id="reviewRating" required><option value="5">5 - Excellent</option><option value="4">4 - Good</option><option value="3">3 - Average</option><option value="2">2 - Poor</option><option value="1">1 - Bad</option></select></div><div class="field"><label for="visitDate">Visit date</label><input id="visitDate" placeholder="May 2026"></div><div class="field"><label for="reviewComment">Comment</label><textarea id="reviewComment" minlength="10" maxlength="1000" required placeholder="What should travellers know?"></textarea></div><button class="primary-button" type="submit">Post review</button></form>
      <form class="card form-grid" id="blogForm"><h3>Publish a story</h3><div class="field"><label for="blogTitle">Title</label><input id="blogTitle" required></div><div class="field"><label for="blogCategory">Category</label><input id="blogCategory" placeholder="Guide, Food, Budget"></div><div class="field"><label for="blogEmoji">Icon text</label><input id="blogEmoji" placeholder="Optional"></div><div class="field"><label for="blogContent">Story</label><textarea id="blogContent" required placeholder="Share your route, costs, tips, or memories"></textarea></div><button class="primary-button" type="submit">Publish story</button></form>
      <form class="card form-grid" id="mediaForm"><h3>Upload media</h3><div class="field"><label for="mediaFile">Photo or video</label><input id="mediaFile" type="file" required accept="image/*,video/*"></div><div class="field"><label for="mediaCaption">Caption</label><textarea id="mediaCaption" placeholder="A short caption for your memory"></textarea></div><button class="primary-button" type="submit">Upload memory</button></form>
    </div>`;
  document.getElementById("reviewForm").addEventListener("submit", BY.submitReview);
  document.getElementById("blogForm").addEventListener("submit", BY.submitBlog);
  document.getElementById("mediaForm").addEventListener("submit", BY.submitMedia);
};

BY.initContributePage = function () {
  const form = document.getElementById("contributeSearchForm");
  const input = document.getElementById("contributeSearchInput");
  const box = document.getElementById("contributeSuggestions");
  const fromUrl = BY.params().get("placeId");
  if (fromUrl) BY.openContributePlace(fromUrl);
  if (!form || !input) return;
  let timer = null;
  input.addEventListener("input", () => {
    clearTimeout(timer);
    timer = setTimeout(async () => {
      const places = await BY.searchPlaces(input.value, "contributeSuggestions", false);
      if (!box) return;
      box.innerHTML = places.length ? places.map(place => `
        <button class="suggestion" type="button" data-contribute-place-id="${BY.escapeHtml(place.id)}">
          <span><strong>${BY.escapeHtml(place.name)}</strong><br><small>${BY.escapeHtml(place.state || "")} · ${BY.escapeHtml(place.type || "Destination")}</small></span>
          <span class="badge light">Select</span>
        </button>`).join("") : `<div class="empty">No destination found.</div>`;
      box.classList.remove("hidden");
      box.querySelectorAll("[data-contribute-place-id]").forEach(btn => btn.addEventListener("click", () => {
        box.classList.add("hidden");
        BY.openContributePlace(btn.dataset.contributePlaceId);
      }));
    }, 260);
  });
  form.addEventListener("submit", async event => {
    event.preventDefault();
    const places = await BY.searchPlaces(input.value, "contributeSuggestions", false);
    if (places[0]) BY.openContributePlace(places[0].id);
    else BY.toast("No matching destination found", "error");
  });
};

BY.openContributePlace = async function (id) {
  if (!id) return BY.toast("Choose a destination", "error");
  const mount = document.getElementById("contributeMount");
  mount.className = "empty";
  mount.textContent = "Loading destination...";
  const place = await BY.loadPlaceForContribution(id);
  if (place) BY.renderContributeForms(place);
};

BY.loadPlaceForContribution = async function (id) {
  try {
    BY.state.place = await BY.request(`/api/places/${id}`);
    return BY.state.place;
  } catch (error) {
    const mount = document.getElementById("contributeMount");
    mount.className = "notice error";
    mount.textContent = "This destination could not be loaded.";
    return null;
  }
};

BY.submitReview = async function (event) {
  event.preventDefault();
  if (!BY.requireLogin()) return;
  const button = event.submitter;
  BY.setBusy(button, true, "Posting...");
  try {
    await BY.request("/api/reviews", { method: "POST", body: JSON.stringify({ placeId: BY.state.place.id, rating: Number(document.getElementById("reviewRating").value), visitDate: document.getElementById("visitDate").value.trim(), comment: document.getElementById("reviewComment").value.trim() }) });
    event.target.reset();
    BY.toast("Review posted", "ok");
  } catch (error) { BY.toast(error.message, "error"); }
  finally { BY.setBusy(button, false); }
};

BY.submitBlog = async function (event) {
  event.preventDefault();
  if (!BY.requireLogin()) return;
  const button = event.submitter;
  BY.setBusy(button, true, "Publishing...");
  try {
    await BY.request("/api/blogs", { method: "POST", body: JSON.stringify({ placeId: BY.state.place.id, title: document.getElementById("blogTitle").value.trim(), category: document.getElementById("blogCategory").value.trim(), emoji: document.getElementById("blogEmoji").value.trim(), content: document.getElementById("blogContent").value.trim() }) });
    event.target.reset();
    BY.toast("Story published", "ok");
  } catch (error) { BY.toast(error.message, "error"); }
  finally { BY.setBusy(button, false); }
};

BY.submitMedia = async function (event) {
  event.preventDefault();
  if (!BY.requireLogin()) return;
  const file = document.getElementById("mediaFile").files[0];
  if (!file) return;
  const body = new FormData();
  body.append("file", file);
  body.append("placeId", BY.state.place.id);
  body.append("caption", document.getElementById("mediaCaption").value.trim());
  const button = event.submitter;
  BY.setBusy(button, true, "Uploading...");
  try {
    await BY.request("/api/media/upload", { method: "POST", body });
    event.target.reset();
    BY.toast("Memory uploaded", "ok");
  } catch (error) { BY.toast(error.message, "error"); }
  finally { BY.setBusy(button, false); }
};
