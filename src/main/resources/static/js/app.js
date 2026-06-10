var BY = window.BY || (window.BY = {});

/* ── Nav update — show/hide Login, Register, Logout ────────── */
BY.updateNav = function () {
  const token        = localStorage.getItem("bharatYatra.token");
  const loginLink    = document.getElementById("loginLink");
  const registerLink = document.getElementById("registerLink");
  const logoutBtn    = document.getElementById("logoutBtn");

  if (token) {
    if (loginLink)    loginLink.style.cssText    = "display:none!important";
    if (registerLink) registerLink.style.cssText = "display:none!important";
    if (logoutBtn)    logoutBtn.style.cssText     = "display:inline-flex!important";
  } else {
    if (loginLink)    loginLink.style.cssText    = "";
    if (registerLink) registerLink.style.cssText = "";
    if (logoutBtn)    logoutBtn.style.cssText     = "display:none!important";
  }
};

/* ── Toast notification ────────────────────────────────────── */
BY.toast = function (message, type = "info") {
  const wrap = document.getElementById("toastWrap");
  if (!wrap) return alert(message);
  const div = document.createElement("div");
  div.className = "toast " + type;
  div.innerText = message;
  wrap.appendChild(div);
  setTimeout(() => div.remove(), 3000);
};

/* ── Button busy state ─────────────────────────────────────── */
BY.setBusy = function (button, state, text = "Loading...") {
  if (!button) return;
  if (state) {
    button.disabled = true;
    button.dataset.text = button.innerText;
    button.innerText = text;
  } else {
    button.disabled = false;
    button.innerText = button.dataset.text || "Submit";
  }
};

/* ── Get current page name from body data attribute ────────── */
BY.getPage = function () {
  return document.body.dataset.page || "index";
};

/* ── Get URL query params ───────────────────────────────────── */
BY.params = function () {
  return new URLSearchParams(window.location.search);
};

/* ── Logout ─────────────────────────────────────────────────── */
BY.logout = function () {
  localStorage.removeItem("bharatYatra.token");
  localStorage.removeItem("bharatYatra.user");
  localStorage.removeItem("bharatYatra.location");
  BY.state.token = null;
  BY.state.user  = null;
  BY.toast("Logged out successfully", "ok");
  setTimeout(() => window.location.href = "login.html", 1000);
};

/* ── Require login — redirect if not authenticated ─────────── */
BY.requireLogin = function () {
  if (!localStorage.getItem("bharatYatra.token")) {
    BY.toast("Please login to continue", "error");
    setTimeout(() => window.location.href = "login.html", 1500);
    return false;
  }
  return true;
};

/*PAGE INITIALISATION */
document.addEventListener("DOMContentLoaded", () => {
	
  BY.updateNav();

  /* Global listeners */
  document.getElementById("logoutBtn")
    ?.addEventListener("click", BY.logout);
  document.getElementById("refreshPopularBtn")
    ?.addEventListener("click", BY.loadPopularPlaces);

  /* Route to page init */
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