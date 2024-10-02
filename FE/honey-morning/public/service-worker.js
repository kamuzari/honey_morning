const CACHE_NAME = 'honey-morning-cache-v1';
const contentToCache = [
  '/',
  '/index.html',
  '/images/tempAppIcon192x192.png',
  '/images/tempAppIcon512x512.png',
];

// Service Worker 설치 단계
self.addEventListener('install', event => {
  console.log('Service Worker installing.');
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => {
      console.log('Caching app shell and content');
      return cache.addAll(contentToCache);
    }),
  );
});

// Service Worker 활성화 단계
self.addEventListener('activate', event => {
  console.log('Service Worker activating.');
  // 이전 캐시 삭제 로직
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cache => {
          if (cache !== CACHE_NAME) {
            console.log('Deleting old cache:', cache);
            return caches.delete(cache);
          }
        }),
      );
    }),
  );
});

// 네트워크 요청 가로채기
self.addEventListener('fetch', event => {
  console.log('Fetching:', event.request.url);
  event.respondWith(
    caches.match(event.request).then(response => {
      // 캐시에 해당 요청이 있으면 캐시에서 반환, 없으면 네트워크 요청
      return response || fetch(event.request);
    }),
  );
});
