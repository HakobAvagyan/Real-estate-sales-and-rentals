(function () {
    'use strict';

    function revealAll() {
        document.querySelectorAll('[data-reveal]').forEach(function (el) {
            el.classList.add('is-visible');
        });
    }

    if (!('IntersectionObserver' in window)) {
        revealAll();
        return;
    }

    var io = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (entry.isIntersecting) {
                entry.target.classList.add('is-visible');
                io.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1, rootMargin: '0px 0px -36px 0px' });

    document.querySelectorAll('[data-reveal-stagger]').forEach(function (container) {
        Array.prototype.forEach.call(container.children, function (child, i) {
            if (!child.hasAttribute('data-reveal')) {
                child.setAttribute('data-reveal', '');
            }
            child.setAttribute('data-reveal-delay', String(Math.min(i, 6)));
        });
    });

    document.querySelectorAll('[data-reveal]').forEach(function (el) {
        io.observe(el);
    });
})();
