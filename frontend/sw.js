const SHELL_CACHE = 'secret-shelf-shell-v3';
const SHELL_FILES = ['/', '/main.js', '/style.css', '/pwa.css', '/vendor/leaflet/leaflet.js', '/vendor/leaflet/leaflet.css', '/manifest.webmanifest', '/icons/icon-192.png', '/icons/icon-512.png', '/icons/apple-touch-icon.png'];

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
        event.waitUntil(caches.open(SHELL_CACHE).then(cache => cache.put('/', cachedResponse)));
      }
      return response;
    }).catch(async () => (await caches.match(request)) || (await caches.match('/')) || caches.match('/index.html')));
    return;
  }
  if (url.pathname === '/main.js' || url.pathname === '/style.css' || url.pathname === '/pwa.css' || url.pathname.startsWith('/vendor/leaflet/') || url.pathname.startsWith('/icons/') || url.pathname === '/manifest.webmanifest') {
    event.respondWith(caches.match(request).then(cached => cached || fetch(request).then(response => {
      if (response.ok) {
        const cachedResponse = response.clone();
        event.waitUntil(caches.open(SHELL_CACHE).then(cache => cache.put(request, cachedResponse)));
      }
      return response;
    })));
  }
});
