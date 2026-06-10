var BY = window.BY || (window.BY = {});


/* ── Escape HTML — prevent XSS ────────────────────────────── */
BY.escapeHtml = function (value) {
  return String(value ?? "").replace(/[&<>"']/g, char => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  }[char]));
};

/* ── Toast notification ────────────────────────────────────── */
BY.toast = function (message, type = "") {
  const wrap = document.getElementById("toastWrap");
  if (!wrap) return alert(message);
  const node = document.createElement("div");
  node.className = `toast ${type}`;
  node.textContent = message;
  wrap.appendChild(node);
  setTimeout(() => node.remove(), 4200);
};

/* ── Button busy state ─────────────────────────────────────── */
BY.setBusy = function (button, busy, label) {
  if (!button) return;
  if (busy) {
    button.dataset.label = button.textContent;
    button.disabled = true;
    button.textContent = label || "Working...";
  } else {
    button.disabled = false;
    button.textContent = button.dataset.label || label || "Done";
  }
};

/* ── Rating display text ───────────────────────────────────── */
BY.ratingText = function (place) {
  return place?.averageRating ? Number(place.averageRating).toFixed(1) : "New";
};

/* ── Gradient fallback ─────────────────────────────────────── */
BY.gradient = function (item) {
  return item?.bgGradient || "linear-gradient(135deg, #27548a, #087f7a)";
};

/* ── Nav update — show/hide Login, Register, Logout ────────── */
/*    MUST be defined before DOMContentLoaded calls it          */
BY.updateNav = function () {
  const token        = localStorage.getItem("bharatYatra.token");
  const loginLink    = document.getElementById("loginLink");
  const registerLink = document.getElementById("registerLink");
  const logoutBtn    = document.getElementById("logoutBtn");

  if (token) {
    /* User is logged in — hide Login & Register, show Logout */
    if (loginLink)    loginLink.style.cssText    = "display:none!important";
    if (registerLink) registerLink.style.cssText = "display:none!important";
    if (logoutBtn)    logoutBtn.style.cssText     = "display:inline-flex!important";
  } else {
    /* User is logged out — show Login & Register, hide Logout */
    if (loginLink)    loginLink.style.cssText    = "";
    if (registerLink) registerLink.style.cssText = "";
    if (logoutBtn)    logoutBtn.style.cssText     = "display:none!important";
  }
};


document.addEventListener("DOMContentLoaded", () => {

  /* Always update nav on every page */
  BY.updateNav();

  /* Global button listeners */
  document.getElementById("logoutBtn")
    ?.addEventListener("click", BY.logout);

  document.getElementById("refreshPopularBtn")
    ?.addEventListener("click", BY.loadPopularPlaces);

  /* Route to page-specific init */
  const page = BY.getPage();

  if (page === "index")          BY.loadPopularPlaces?.();
  if (page === "place")          BY.loadPlace?.(BY.params().get("id"));
  if (page === "nearby")         BY.initNearbyPage?.();
  if (page === "contribute")     BY.initContributePage?.();
  if (page === "login")          BY.initLoginPage?.();
  if (page === "register")       BY.initRegisterPage?.();
  if (page === "forgot")         BY.initForgotPasswordPage?.();
  if (page === "verify-otp")     BY.initVerifyOtpPage?.();
  if (page === "reset-password") BY.initResetPasswordPage?.();
});