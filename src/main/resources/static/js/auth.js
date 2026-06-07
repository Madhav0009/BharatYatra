var BY = window.BY || (window.BY = {});

/* =========================
   LOGIN
========================= */

BY.initLoginPage = function () {
  const form = document.getElementById("loginForm");
  if (!form || form.dataset.bound === "true") return;

  form.dataset.bound = "true";

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const btn = e.submitter;
    if (btn.disabled) return;

    BY.setBusy(btn, true, "Logging in...");

    try {
      const res = await BY.request("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({
          email: document.getElementById("loginEmail").value.trim(),
          password: document.getElementById("loginPassword").value
        })
      });

      localStorage.setItem("bharatYatra.token", res.token);
      BY.toast("Login Success", "ok");

      window.location.href = "index.html";

    } catch (err) {
      BY.toast(err.message, "error");
    } finally {
      BY.setBusy(btn, false);
    }
  });
};

/* =========================
   REGISTER
========================= */

BY.initRegisterPage = function () {
  const form = document.getElementById("registerForm");
  if (!form || form.dataset.bound === "true") return;

  form.dataset.bound = "true";

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const btn = e.submitter;

    BY.setBusy(btn, true, "Creating...");

    try {
      const res = await BY.request("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({
          fullName: document.getElementById("registerName").value.trim(),
          email: document.getElementById("registerEmail").value.trim(),
          password: document.getElementById("registerPassword").value,
          city: document.getElementById("registerCity")?.value?.trim()
        })
      });

      BY.toast("Account Created", "ok");
      window.location.href = "login.html";

    } catch (err) {
      BY.toast(err.message, "error");
    } finally {
      BY.setBusy(btn, false);
    }
  });
};

/* =========================
   FORGOT PASSWORD APIs
========================= */

BY.sendOtp = (email) =>
  BY.request(`/api/auth/send-otp?email=${encodeURIComponent(email)}`, { method: "POST" });

BY.verifyOtp = (email, otp) =>
  BY.request(`/api/auth/verify-otp?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otp)}`, { method: "POST" });

BY.resetPassword = (email, pass) =>
  BY.request(`/api/auth/reset-password?email=${encodeURIComponent(email)}&newPassword=${encodeURIComponent(pass)}`, { method: "POST" });

/* =========================
   STEP 1: FORGOT PASSWORD
========================= */

BY.initForgotPasswordPage = function () {
  const form = document.getElementById("forgotForm");
  if (!form || form.dataset.bound === "true") return;

  form.dataset.bound = "true";

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const btn = e.submitter;
    if (btn.disabled) return;

    const email = document.getElementById("forgotEmail").value.trim();

    BY.setBusy(btn, true, "Sending OTP...");

    try {
      await BY.sendOtp(email);

      BY.toast("OTP sent");

      window.location.href =
        "verify-otp.html?email=" + encodeURIComponent(email);

    } catch (err) {
      BY.toast(err.message, "error");
    } finally {
      BY.setBusy(btn, false);
    }
  });
};

/* =========================
   STEP 2: VERIFY OTP
========================= */

BY.initVerifyOtpPage = function () {
  const form = document.getElementById("otpForm");
  if (!form || form.dataset.bound === "true") return;

  form.dataset.bound = "true";

  const email = BY.params().get("email");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const btn = e.submitter;
    if (btn.disabled) return;

    const otp = document.getElementById("otp").value.trim();

    BY.setBusy(btn, true, "Verifying...");

    try {
      await BY.verifyOtp(email, otp);

      BY.toast("Verified");

      window.location.href =
        "reset-password.html?email=" + encodeURIComponent(email);

    } catch (err) {
      BY.toast(err.message, "error");
    } finally {
      BY.setBusy(btn, false);
    }
  });
};

/* =========================
   STEP 3: RESET PASSWORD
========================= */

BY.initResetPasswordPage = function () {
  const form = document.getElementById("resetForm");
  if (!form || form.dataset.bound === "true") return;

  form.dataset.bound = "true";

  const email = BY.params().get("email");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const btn = e.submitter;
    if (btn.disabled) return;

    const pass = document.getElementById("newPassword").value;

    BY.setBusy(btn, true, "Resetting...");

    try {
      await BY.resetPassword(email, pass);

      BY.toast("Password Reset Success");

      window.location.href = "login.html";

    } catch (err) {
      BY.toast(err.message, "error");
    } finally {
      BY.setBusy(btn, false);
    }
  });
  
  BY.requireLogin = function () {
    if (!BY.state.token) {
      BY.toast("Please login to continue", "error");
      setTimeout(() => window.location.href = "login.html", 1500);
      return false;
    }
    return true;
  };
  
  BY.logout = function () {
    localStorage.removeItem("bharatYatra.token");
    localStorage.removeItem("bharatYatra.user");
    BY.state.token = null;
    BY.state.user = null;
    BY.toast("Logged out", "ok");
    setTimeout(() => window.location.href = "login.html", 1000);
  };
};