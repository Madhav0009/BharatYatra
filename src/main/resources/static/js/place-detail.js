var BY = window.BY || (window.BY = {});

/* ─────────────────────────────────────────────
   DATE / TIME
───────────────────────────────────────────── */

/**
 * Formats an ISO date string into a human-readable format.
 * e.g. "2026-06-02T19:47:26.344129" → "2 June 2026, 7:47 PM"
 */
BY.formatDateTime = function (dateString) {
  if (!dateString) return "";

  return new Date(dateString).toLocaleString("en-IN", {
    day   : "numeric",
    month : "long",
    year  : "numeric",
    hour  : "numeric",
    minute: "2-digit",
    hour12: true,
  });
};


/* ─────────────────────────────────────────────
   PLACE LOADING
───────────────────────────────────────────── */

BY.loadPlace = async function (id) {
  const mount = document.getElementById("detailMount");

  if (!id) {
    mount.innerHTML = `<div class="notice error">Destination not found</div>`;
    return;
  }

  try {
    const params = new URLSearchParams();
    const city   = localStorage.getItem("bharatYatra.city") || "Bengaluru";

    params.set("city", city);

    if (BY.state.location?.lat) {
      params.set("lat", BY.state.location.lat);
      params.set("lon", BY.state.location.lon);
    }

    const url   = `/api/places/${id}` + (params.toString() ? `?${params}` : "");
    const place = await BY.request(url);

    BY.state.place = place;
    BY.renderPlaceDetail(place);

    await Promise.allSettled([
      BY.loadReviews(id),
      BY.loadBlogs(id),
      BY.loadMedia(id),
    ]);

  } catch (e) {
    console.error(e);
    mount.innerHTML = `<div class="notice error">${e.message}</div>`;
  }
};


/* ─────────────────────────────────────────────
   PLACE RENDER
───────────────────────────────────────────── */

BY.renderPlaceDetail = function (p) {
  const titleEl    = document.getElementById("placeTitle");
  const subtitleEl = document.getElementById("placeSubtitle");
  const mount      = document.getElementById("detailMount");

  if (titleEl)    titleEl.textContent    = p.name;
  if (subtitleEl) subtitleEl.textContent = p.tagline || `${p.name}, ${p.state}`;
  if (!mount)     return;

  const tabs = ["overview", "routes", "nearby", "hotels", "facilities", "media", "blogs", "reviews"];

  mount.className = "";
  mount.innerHTML = `
    <div class="detail-layout">

      <article>

        <!-- Hero -->
        <div class="detail-hero" style="background:${BY.escapeHtml(BY.gradient(p))}">
          <div class="detail-emoji">${BY.escapeHtml(p.emoji || "IN")}</div>
          <div class="detail-hero-content">
            <span class="badge">
              ${BY.escapeHtml(p.type || "Destination")} · ${BY.escapeHtml(p.state || "")}
            </span>
            <h2>${BY.escapeHtml(p.name)}</h2>
            <div class="meta-row">
              <span class="badge">Rating ${BY.escapeHtml(BY.ratingText(p))}</span>
              <span class="badge">${BY.escapeHtml(p.totalReviews || 0)} reviews</span>
              <span class="badge">Founded ${BY.escapeHtml(p.founded || "N/A")}</span>
            </div>
            <p>${BY.escapeHtml(p.description || p.tagline || "")}</p>
          </div>
        </div>

        <!-- Tabs -->
        <div class="content-tabs">
          ${tabs.map((tab, i) => `
            <button
              class="tab-button ${i === 0 ? "active" : ""}"
              type="button"
              data-tab="${tab}"
            >
              ${tab[0].toUpperCase() + tab.slice(1)}
            </button>
          `).join("")}
        </div>

        <!-- Panels -->
        <div class="tab-panel active" data-panel="overview">
          ${BY.renderOverview(p)}
        </div>
        <div class="tab-panel" data-panel="routes">
          ${BY.renderRoutes(p.travelRoutes || [])}
        </div>
        <div class="tab-panel" data-panel="nearby">
          ${BY.renderNearby(p.nearbySpots || [], p.nextPlaces || [])}
        </div>
        <div class="tab-panel" data-panel="hotels">
          ${BY.renderHotels(p.hotels || [])}
        </div>
        <div class="tab-panel" data-panel="facilities">
          ${BY.renderFacilities(p.facilities || [])}
        </div>
        <div class="tab-panel" data-panel="media">
          <div id="mediaMount" class="empty">Loading media...</div>
        </div>
        <div class="tab-panel" data-panel="blogs">
          <div id="blogsMount" class="empty">Loading blogs...</div>
        </div>
        <div class="tab-panel" data-panel="reviews">
          <div id="reviewsMount" class="empty">Loading reviews...</div>
        </div>

      </article>

      <!-- Sidebar -->
      <aside class="side-stack">

        <div class="panel">
          <h3>Travel basics</h3>
          <div class="info-list">
            <div class="info-line">
              <span>Airport</span>
              <strong>${BY.escapeHtml(p.nearestAirport  || "N/A")}</strong>
            </div>
            <div class="info-line">
              <span>Railway</span>
              <strong>${BY.escapeHtml(p.nearestRailway  || "N/A")}</strong>
            </div>
            <div class="info-line">
              <span>Bus stand</span>
              <strong>${BY.escapeHtml(p.nearestBusStand || "N/A")}</strong>
            </div>
          </div>
        </div>

        <div class="panel">
          <h3>Actions</h3>
          <div class="form-grid">
            <a class="primary-button" href="contribute.html?placeId=${encodeURIComponent(p.id)}">
              Contribute
            </a>
            <a
              class="ghost-button"
              target="_blank"
              rel="noopener"
              href="https://maps.google.com/?q=${encodeURIComponent(
                p.latitude && p.longitude
                  ? `${p.latitude},${p.longitude}`
                  : `${p.name} ${p.state}`
              )}"
            >
              Open map
            </a>
          </div>
        </div>

      </aside>

    </div>
  `;

  mount.querySelectorAll("[data-tab]").forEach(button =>
    button.addEventListener("click", () => BY.activateTab(button.dataset.tab))
  );

  BY.bindPlaceLinks(mount);
};

BY.activateTab = function (tab) {
  document.querySelectorAll("[data-tab]").forEach(node =>
    node.classList.toggle("active", node.dataset.tab === tab)
  );
  document.querySelectorAll("[data-panel]").forEach(node =>
    node.classList.toggle("active", node.dataset.panel === tab)
  );
};


/* ─────────────────────────────────────────────
   TAB PANEL RENDERERS
───────────────────────────────────────────── */

BY.renderOverview = function (p) {
  const timelineHtml = (p.timeline || []).length
    ? `<div class="timeline">
        ${p.timeline.map(t => `
          <div>
            <strong>${BY.escapeHtml(t.year)}</strong>
            <p>${BY.escapeHtml(t.description)}</p>
          </div>
        `).join("")}
       </div>`
    : `<p>No timeline added yet.</p>`;

  return `
    <div class="grid cards-2">
      <div class="card">
        <h3>About</h3>
        <p>${BY.escapeHtml(p.description || "No description available.")}</p>
        ${p.descriptionExtra ? `<p>${BY.escapeHtml(p.descriptionExtra)}</p>` : ""}
      </div>
      <div class="card">
        <h3>Timeline</h3>
        ${timelineHtml}
      </div>
    </div>
  `;
};

BY.renderRoutes = function (routes) {
  if (!routes.length) return `<div class="empty">No routes found.</div>`;

  return `
    <div class="grid cards-2">
      ${routes.map(r => `
        <div class="card">
          <span class="badge light">
            ${BY.escapeHtml(r.mode || "Route")}${r.fastest ? " · Fastest" : ""}
          </span>
          <h3>${BY.escapeHtml(r.fromCity || "Your city")}</h3>
          <p>${BY.escapeHtml(r.routeNote || "Route details available.")}</p>
          <div class="card-footer">
            <strong>${BY.escapeHtml(r.duration || "N/A")} · ${BY.escapeHtml(r.distance || "N/A")}</strong>
            ${r.bookingUrl
              ? `<a class="ghost-button" href="${BY.escapeHtml(r.bookingUrl)}" target="_blank" rel="noopener">Book</a>`
              : ""}
          </div>
        </div>
      `).join("")}
    </div>
  `;
};

BY.renderNearby = function (spots, next) {
  const spotsHtml = spots.length
    ? spots.map(s => `
        <div class="info-line">
          <span>${BY.escapeHtml(s.icon || "")} ${BY.escapeHtml(s.name)}</span>
          <strong>${BY.escapeHtml(s.distanceKm || s.type || "")}</strong>
        </div>
      `).join("")
    : `<p>No nearby spots listed.</p>`;

  const nextHtml = next.length
    ? next.map(n => `
        <button class="suggestion" data-place-id="${BY.escapeHtml(n.id)}" type="button">
          <span>
            <strong>${BY.escapeHtml(n.name)}</strong><br>
            <small>${BY.escapeHtml(n.state || "")}</small>
          </span>
          <span class="badge light">${BY.escapeHtml(BY.ratingText(n))}</span>
        </button>
      `).join("")
    : `<p>No next places listed.</p>`;

  return `
    <div class="grid cards-2">
      <div class="card">
        <h3>Nearby spots</h3>
        ${spotsHtml}
      </div>
      <div class="card">
        <h3>Next destinations</h3>
        ${nextHtml}
      </div>
    </div>
  `;
};

BY.renderHotels = function (hotels) {
  if (!hotels.length) return `<div class="empty">No hotels listed.</div>`;

  return `
    <div class="grid cards-3">
      ${hotels.map(h => `
        <div class="card">
          <span class="badge light">${BY.escapeHtml(h.stars || 0)} stars</span>
          <h3>${BY.escapeHtml(h.name)}</h3>
          <p>${BY.escapeHtml(h.description || "")}</p>
          <div class="card-footer">
            <strong>${h.pricePerNight ? `Rs. ${BY.escapeHtml(h.pricePerNight)}` : "Price N/A"}</strong>
            ${h.bookingUrl
              ? `<a class="ghost-button" href="${BY.escapeHtml(h.bookingUrl)}" target="_blank" rel="noopener">Book</a>`
              : ""}
          </div>
        </div>
      `).join("")}
    </div>
  `;
};

BY.renderFacilities = function (facilities) {
  if (!facilities.length) return `<div class="empty">No facilities listed.</div>`;

  return `
    <div class="grid cards-3">
      ${facilities.map(f => `
        <div class="card">
          <span class="badge light">${BY.escapeHtml(f.tag || "Facility")}</span>
          <h3>${BY.escapeHtml(f.icon || "")} ${BY.escapeHtml(f.name)}</h3>
          <p>${BY.escapeHtml(f.description || "")}</p>
        </div>
      `).join("")}
    </div>
  `;
};


/* ─────────────────────────────────────────────
   ASYNC DATA LOADERS
───────────────────────────────────────────── */

BY.loadReviews = async function (id) {
  const mount = document.getElementById("reviewsMount");
  if (!mount) return;

  try {
    const data    = await BY.request(`/api/reviews/place/${id}?page=0&size=10`);
    const reviews = BY.pageItems(data);

    mount.className = "";
    mount.innerHTML = reviews.length
      ? `
          <div class="grid cards-2">
            ${reviews.map(r => `
              <div class="card">
                <span class="badge light">${BY.escapeHtml(r.rating)} / 5</span>
                <h3>${BY.escapeHtml(r.userName || "Traveller")}</h3>
                <p>${BY.escapeHtml(r.comment)}</p>
                <div class="card-footer">
                  <small>${BY.escapeHtml(r.visitDate || r.createdAt || "")}</small>
                  ${BY.state.user
                    ? `<button class="danger-button" type="button" data-delete-review="${BY.escapeHtml(r.id)}">Delete</button>`
                    : ""}
                </div>
              </div>
            `).join("")}
          </div>
        `
      : `<div class="empty">No reviews yet.</div>`;

    mount.querySelectorAll("[data-delete-review]").forEach(button =>
      button.addEventListener("click", () => BY.deleteReview(button.dataset.deleteReview))
    );

  } catch (e) {
    mount.className  = "notice error";
    mount.textContent = e.message;
  }
};

BY.loadBlogs = async function (id) {
  const mount = document.getElementById("blogsMount");
  if (!mount) return;

  try {
    const data  = await BY.request(`/api/blogs/place/${id}?page=0&size=10`);
    const blogs = BY.pageItems(data);

    mount.className = "";
    mount.innerHTML = blogs.length
      ? `
          <div class="grid cards-2">
            ${blogs.map(b => `
              <article class="card">
                <span class="badge light">${BY.escapeHtml(b.category || "Story")}</span>
                <h3>${BY.escapeHtml(b.title)}</h3>
                <p>${BY.escapeHtml(b.content)}</p>
                <div class="card-footer">
                  <small>By ${BY.escapeHtml(b.authorName || "Traveller")}</small>
                  <small>${BY.formatDateTime(b.createdAt)}</small>
                </div>
              </article>
            `).join("")}
          </div>
        `
      : `<div class="empty">No blogs yet.</div>`;

  } catch (e) {
    mount.className  = "notice error";
    mount.textContent = e.message;
  }
};

BY.loadMedia = async function (id) {
  const mount = document.getElementById("mediaMount");
  if (!mount) return;

  try {
    const data  = await BY.request(`/api/media/place/${id}?page=0&size=20`);
    const media = BY.pageItems(data);

    mount.className = "";
    mount.innerHTML = media.length
      ? `
          <div class="media-grid">
            ${media.map(item => `
              <div class="media-item">
                ${String(item.mediaType || "").includes("VIDEO")
                  ? `<video src="${BY.escapeHtml(item.fileUrl)}" controls></video>`
                  : `<img src="${BY.escapeHtml(item.fileUrl)}" alt="${BY.escapeHtml(item.caption || item.fileName || "Media")}">`
                }
                <span>${BY.escapeHtml(item.caption || item.fileName || "")}</span>
              </div>
            `).join("")}
          </div>
        `
      : `<div class="empty">No photos or videos uploaded yet.</div>`;

  } catch (e) {
    mount.className  = "notice error";
    mount.textContent = e.message;
  }
};


/* ─────────────────────────────────────────────
   ACTIONS
───────────────────────────────────────────── */

BY.deleteReview = async function (id) {
  if (!BY.requireLogin()) return;

  try {
    await BY.request(`/api/reviews/${id}`, { method: "DELETE" });
    BY.toast("Review deleted", "ok");
    BY.loadReviews(BY.state.place.id);
  } catch (e) {
    BY.toast(e.message, "error");
  }
};
