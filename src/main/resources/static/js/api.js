var BY = window.BY || (window.BY = {});

BY.apiUrl = function (path) {
  return `${BY.state.apiBase.replace(/\/$/, "")}${path}`;
};

BY.request = async function (path, options = {}) {
  const headers = new Headers(options.headers || {});
  if (!(options.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  if (BY.state.token) headers.set("Authorization", `Bearer ${BY.state.token}`);

  const response = await fetch(BY.apiUrl(path), { ...options, headers });

  //  ADDED — auto-logout when token is expired or invalid
  if (response.status === 401 || response.status === 403) {
    BY.logout();
    BY.toast("Session expired. Please login again.", "error");
    setTimeout(() => window.location.href = "login.html", 1500);
    throw new Error("Session expired. Please login again.");
  }

  const text = await response.text();
  let data = null;
  if (text) {
    try { data = JSON.parse(text); } catch { data = text; }
  }
  if (!response.ok) {
    const message = data?.message || data?.error || data || `Request failed with ${response.status}`;
    throw new Error(message);
  }
  return data;
};

BY.pageItems = function (payload) {
  return Array.isArray(payload) ? payload : (payload?.content || []);
};