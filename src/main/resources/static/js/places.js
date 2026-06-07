var BY = window.BY || (window.BY = {});

/* ── Place image lookup table ─────────────────────────────────────────────── */
BY.placeImages = {
  "Hampi":      "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=700&q=80",
  "Goa":        "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=700&q=80",
  "Agra":       "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=700&q=80",
  "Leh Ladakh": "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=700&q=80",
  "Kerala":     "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=700&q=80",
  "Jaipur":     "https://images.unsplash.com/photo-1477587458883-47145ed94245?w=700&q=80",
  "Varanasi":   "https://images.unsplash.com/photo-1570458436416-b8fcccfe883f?w=700&q=80",
  "Manali":     "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?w=700&q=80",
  "Taj Mahal":  "https://images.unsplash.com/photo-1548013146-72479768bada?w=700&q=80",
  "Mysore":     "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=700&q=80",
};

/* ── Place card component ─────────────────────────────────────────────────── */
BY.placeCard = function (place) {
  const imgUrl = place.imageUrl
    || BY.placeImages[place.name]
    || null;

  const artStyle = imgUrl
    ? `background-image:url('${imgUrl}');`
    : `background:${BY.escapeHtml(BY.gradient(place))};`;

  return `
    <button class="place-card" type="button" data-place-id="${BY.escapeHtml(place.id)}">
      <div class="place-art" style="${artStyle}"></div>
      <div class="place-emoji">${BY.escapeHtml(place.emoji || "")}</div>
      <div class="place-body">
        <span class="badge">${BY.escapeHtml(place.type || "Destination")} · ${BY.escapeHtml(BY.ratingText(place))}</span>
        <h3>${BY.escapeHtml(place.name)}</h3>
        <p>${BY.escapeHtml(place.state || "")}</p>
        <p>${BY.escapeHtml(place.tagline || "Explore this destination")}</p>
      </div>
    </button>`;
};

/* ── Bind place links ─────────────────────────────────────────────────────── */
BY.bindPlaceLinks = function (root = document) {
  root.querySelectorAll("[data-place-id]").forEach(button => {
    button.addEventListener("click", () => {
      window.location.href = `place.html?id=${encodeURIComponent(button.dataset.placeId)}`;
    });
  });
};

/* ── Load popular places ──────────────────────────────────────────────────── */
BY.loadPopularPlaces = async function () {
  const grid = document.getElementById("placesGrid");
  if (!grid) return;
  grid.innerHTML = `<div class="empty">Loading popular destinations...</div>`;
  try {
    const places = await BY.request("/api/places/popular");
    grid.innerHTML = places.length ? places.map(BY.placeCard).join("") : `<div class="empty">No popular destinations are available yet.</div>`;
    BY.bindPlaceLinks(grid);
  } catch (error) {
    grid.innerHTML = `<div class="notice error">Destinations could not be loaded right now. Please try again in a moment.</div>`;
  }
};

/* ── Search ───────────────────────────────────────────────────────────────── */
BY.searchPlaces = async function (query, boxId = "suggestions", showDropdown = true) {
  const box = document.getElementById(boxId);
  if (!query.trim()) {
    box?.classList.add("hidden");
    return [];
  }
  try {
    const result = await BY.request(`/api/places/search?query=${encodeURIComponent(query.trim())}`);
    const places = result?.places || [];
    if (box && showDropdown) {
      box.innerHTML = places.length ? places.map(place => `
        <button class="suggestion" type="button" data-place-id="${BY.escapeHtml(place.id)}">
          <span><strong>${BY.escapeHtml(place.name)}</strong><br><small>${BY.escapeHtml(place.state || "")} · ${BY.escapeHtml(place.type || "Destination")}</small></span>
          <span class="badge light">${BY.escapeHtml(BY.ratingText(place))}</span>
        </button>`).join("") : `<div class="empty">No destination found for "${BY.escapeHtml(query)}".</div>`;
      box.classList.remove("hidden");
      BY.bindPlaceLinks(box);
    }
    return places;
  } catch (error) {
    BY.toast("Search is unavailable right now", "error");
    return [];
  }
};

/* ── Search init ──────────────────────────────────────────────────────────── */
BY.initSearch = function () {
  const form = document.getElementById("searchForm");
  const input = document.getElementById("searchInput");
  if (!form || !input) return;
  let timer = null;
  input.addEventListener("input", () => {
    clearTimeout(timer);
    timer = setTimeout(() => BY.searchPlaces(input.value), 260);
  });
  form.addEventListener("submit", async event => {
    event.preventDefault();
    const places = await BY.searchPlaces(input.value, "suggestions", false);
    if (places[0]) window.location.href = `place.html?id=${encodeURIComponent(places[0].id)}`;
    else BY.toast("No matching destination found", "error");
  });
  document.addEventListener("click", event => {
    if (!event.target.closest(".search-shell")) document.querySelectorAll(".suggestions").forEach(node => node.classList.add("hidden"));
  });
};

/* ── Geolocation ──────────────────────────────────────────────────────────── */
BY.detectLocation = function () {
  if (!navigator.geolocation) {
    BY.toast("Location is not supported in this browser", "error");
    return Promise.resolve(false);
  }
  return new Promise(resolve => {
    navigator.geolocation.getCurrentPosition(pos => {
      BY.state.location = {
        lat: Number(pos.coords.latitude.toFixed(6)),
        lon: Number(pos.coords.longitude.toFixed(6))
      };
      localStorage.setItem("bharatYatra.location", JSON.stringify(BY.state.location));
      document.getElementById("latInput") && (document.getElementById("latInput").value = BY.state.location.lat);
      document.getElementById("lonInput") && (document.getElementById("lonInput").value = BY.state.location.lon);
      BY.toast("Location added", "ok");
      resolve(true);
    }, () => {
      BY.toast("Location permission was not granted", "error");
      resolve(false);
    }, { enableHighAccuracy: true, timeout: 10000 });
  });
};

/* ── Nearby page ──────────────────────────────────────────────────────────── */
BY.initNearbyPage = function () {
  const button = document.getElementById("nearbyBtn");
  const detect = document.getElementById("detectLocationBtn");
  const grid = document.getElementById("nearbyGrid");
  if (!button || !grid) return;
  if (BY.state.location?.lat && BY.state.location?.lon) {
    document.getElementById("latInput").value = BY.state.location.lat;
    document.getElementById("lonInput").value = BY.state.location.lon;
  }
  detect?.addEventListener("click", BY.detectLocation);
  button.addEventListener("click", async () => {
    BY.setBusy(button, true, "Finding...");
    try {
      let lat = document.getElementById("latInput").value.trim();
      let lon = document.getElementById("lonInput").value.trim();
      if (!lat || !lon) {
        const ok = await BY.detectLocation();
        if (!ok) return;
        lat = BY.state.location.lat;
        lon = BY.state.location.lon;
      }
      const radius = document.getElementById("radiusInput").value || 500;
      const places = await BY.request(`/api/places/nearby?lat=${encodeURIComponent(lat)}&lon=${encodeURIComponent(lon)}&radiusKm=${encodeURIComponent(radius)}&limit=8`);
      grid.innerHTML = places.length ? places.map(BY.placeCard).join("") : `<div class="empty">No destinations found in this radius.</div>`;
      BY.bindPlaceLinks(grid);
    } catch (error) {
      grid.innerHTML = `<div class="notice error">Nearby destinations could not be loaded.</div>`;
    } finally {
      BY.setBusy(button, false);
    }
  });
};