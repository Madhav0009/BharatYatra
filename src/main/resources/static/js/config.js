var BY = window.BY || (window.BY = {});

BY.state = {
  apiBase: localStorage.getItem("bharatYatra.apiBase") || (location.protocol.startsWith("http") ? location.origin : "http://localhost:8080"),
  token: localStorage.getItem("bharatYatra.token") || "",
  user: JSON.parse(localStorage.getItem("bharatYatra.user") || "null"),
  location: JSON.parse(localStorage.getItem("bharatYatra.location") || "null"),
  place: null
};

BY.getPage = function () { return document.body.dataset.page || "index"; };
BY.params = function () { return new URLSearchParams(window.location.search); };
BY.setApiBase = function (url) {
  BY.state.apiBase = (url || BY.state.apiBase).replace(/\/$/, "");
  localStorage.setItem("bharatYatra.apiBase", BY.state.apiBase);
};
