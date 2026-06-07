Bharat Yatra Frontend

A clean multi-page travel frontend for the Bharat Yatra application.

Pages:
- index.html        Discover and search destinations
- login.html        User login
- register.html     User registration
- nearby.html       Find destinations near user location
- place.html        Destination guide page. Opens as place.html?id=1
- contribute.html   Search a destination, then add review, story, or media

CSS files:
- css/style.css       Imports the CSS modules
- css/base.css        Theme, reset, typography
- css/layout.css      Header, hero, grids, footer, page layout
- css/components.css  Buttons, cards, badges, notices, toasts
- css/forms.css       Inputs, search, auth and contribution forms
- css/pages.css       Destination detail tabs, timeline, media gallery
- css/responsive.css  Tablet and mobile rules

JavaScript files:
- js/config.js        Shared state and automatic API base selection
- js/api.js           Request helper
- js/ui.js            UI helpers and session navigation
- js/auth.js          Login, register, logout and JWT storage
- js/places.js        Destination search, popular places and nearby places
- js/place-detail.js  Destination guide rendering, reviews, blogs and media
- js/contribute.js    Review, story and media upload forms
- js/app.js           Page initialization

Usage:
1. Start the Bharat Yatra server.
2. Open index.html directly, or copy this folder into src/main/resources/static.
3. If served by the application, it automatically uses the same host for requests.
