const SHELL_CACHE = 'secret-shelf-pages-v1';
const BASE_URL = new URL('./', self.registration.scope);
const HOME_URL = new URL('./', BASE_URL).toString();
const SHELL_FILES = ['', 'main.js', 'style.css', 'pwa.css', 'vendor/leaflet/leaflet.js', 'vendor/leaflet/leaflet.css', 'manifest.webmanifest', 'icons/icon-192.png', 'icons/icon-512.png', 'icons/apple-touch-icon.png'].map(file => new URL(file, BASE_URL).toString());

self.addEventListener('install', event => {
  event.waitUntil(caches.open(SHELL_CACHE).then(cache => cache.addAll(SHELL_FILES)));
  self.skipWaiting();
});

self.addEventListener('activate', event => {
  event.waitUntil(Promise.all([self.clients.claim(), caches.keys().then(keys => Promise.all(
    keys.filter(key => key.startsWith('secret-shelf-') && key !== SHELL_CACHE).map(key => caches.delete(key))
  ))]));
});

self.addEventListener('fetch', event => {
  const request = event.request, url = new URL(request.url);
  if (request.method !== 'GET' || url.origin !== self.location.origin) return;
  if (request.mode === 'navigate') {
    event.respondWith(fetch(request).then(response => {
      if (response.ok) {
        const cachedResponse = response.clone();
        event.waitUntil(caches.open(SHELL_CACHE).then(cache => cache.put(HOME_URL, cachedResponse)));
      }
      return response;
    }).catch(async () => (await caches.match(request)) || (await caches.match(HOME_URL)) || caches.match(new URL('index.html', BASE_URL).toString())));
    return;
  }
  const cacheable = ['main.js', 'style.css', 'pwa.css', 'manifest.webmanifest', 'icons/icon-192.png', 'icons/icon-512.png', 'icons/apple-touch-icon.png'].some(file => url.pathname === new URL(file, BASE_URL).pathname)
    || url.pathname.startsWith(new URL('vendor/leaflet/', BASE_URL).pathname);
  if (!cacheable) return;
  event.respondWith(caches.match(request).then(cached => cached || fetch(request).then(response => {
    if (response.ok) {
      const cachedResponse = response.clone();
      event.waitUntil(caches.open(SHELL_CACHE).then(cache => cache.put(request, cachedResponse)));
    }
    return response;
  })));
});
