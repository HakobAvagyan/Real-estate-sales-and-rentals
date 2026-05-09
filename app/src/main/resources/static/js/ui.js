(function () {
    'use strict';

    /* ─── Module 1: Mobile Nav ─────────────────────────────────────────── */
    (function initMobileNav() {
        var hamburger = document.querySelector('.nav-hamburger');
        var drawer    = document.getElementById('mobileNavDrawer');
        var overlay   = document.getElementById('mobileNavOverlay');
        if (!hamburger || !drawer) return;

        function openNav() {
            drawer.classList.add('open');
            if (overlay) overlay.classList.add('open');
            hamburger.setAttribute('aria-expanded', 'true');
        }
        function closeNav() {
            drawer.classList.remove('open');
            if (overlay) overlay.classList.remove('open');
            hamburger.setAttribute('aria-expanded', 'false');
        }

        hamburger.addEventListener('click', openNav);
        if (overlay) overlay.addEventListener('click', closeNav);

        var closeBtn = drawer.querySelector('.mobile-nav-close');
        if (closeBtn) closeBtn.addEventListener('click', closeNav);

        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && drawer.classList.contains('open')) closeNav();
        });
    })();

    /* ─── Module 2: Filter Panel ───────────────────────────────────────── */
    (function initFilterPanel() {
        if (!document.getElementById('filterPanel')) return;

        window.openFilterPanel = function () {
            document.getElementById('filterPanel').classList.add('open');
            var overlay = document.getElementById('filterOverlay');
            if (overlay) overlay.classList.add('open');
            document.body.classList.add('filter-panel-active');
        };
        window.closeFilterPanel = function () {
            document.getElementById('filterPanel').classList.remove('open');
            var overlay = document.getElementById('filterOverlay');
            if (overlay) overlay.classList.remove('open');
            document.body.classList.remove('filter-panel-active');
        };

        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                var panel = document.getElementById('filterPanel');
                if (panel && panel.classList.contains('open')) window.closeFilterPanel();
            }
        });
    })();

    /* ─── Module 3: Phone Popup ────────────────────────────────────────── */
    (function initPhonePopup() {
        var popup = document.getElementById('phonePopup');
        if (!popup) {
            window.openPhonePopup = function () {};
            window.closePhonePopup = function () {};
            return;
        }

        window.openPhonePopup = function (btn) {
            var phone = btn && btn.dataset ? btn.dataset.phone : '';
            var emptyMsg = (window.__i18nHome && window.__i18nHome.phoneNotProvided)
                ? window.__i18nHome.phoneNotProvided
                : 'Not provided';
            var numEl = document.getElementById('phonePopupNumber');
            if (numEl) numEl.textContent = phone || emptyMsg;
            popup.classList.add('open');
        };
        window.closePhonePopup = function () {
            popup.classList.remove('open');
        };

        popup.addEventListener('click', function (e) {
            if (e.target === popup) window.closePhonePopup();
        });
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && popup.classList.contains('open')) window.closePhonePopup();
        });
    })();

    /* ─── Module 4: Own Listing Toast ─────────────────────────────────── */
    (function initOwnListingToast() {
        var toast = document.getElementById('ownListingToast');
        if (!toast) {
            window.showOwnListing = function (e) { if (e) e.preventDefault(); };
            return;
        }
        window.showOwnListing = function (event) {
            if (event) event.preventDefault();
            toast.classList.add('show');
            clearTimeout(toast._timer);
            toast._timer = setTimeout(function () { toast.classList.remove('show'); }, 3000);
        };
    })();

    /* ─── Module 5: Lightbox ───────────────────────────────────────────── */
    (function initLightbox() {
        var lightbox = document.getElementById('imageLightbox');
        if (!lightbox) return;

        var preview  = document.getElementById('imageLightboxPreview');
        var closeBtn = document.getElementById('imageLightboxClose');
        var prevBtn  = document.getElementById('imageLightboxPrev');
        var nextBtn  = document.getElementById('imageLightboxNext');
        var images   = Array.prototype.slice.call(document.querySelectorAll('[data-full-image]'));
        var currentIndex = -1;

        function showAt(index) {
            if (!images.length) return;
            if (index < 0)            index = images.length - 1;
            if (index >= images.length) index = 0;
            currentIndex = index;
            preview.src = images[currentIndex].dataset.fullImage;
            lightbox.classList.add('open');
            lightbox.setAttribute('aria-hidden', 'false');
        }
        function close() {
            lightbox.classList.remove('open');
            lightbox.setAttribute('aria-hidden', 'true');
            preview.src = '';
            currentIndex = -1;
        }

        document.addEventListener('click', function (e) {
            var t = e.target;
            if (t && t.dataset && t.dataset.fullImage) {
                showAt(images.indexOf(t));
                return;
            }
            if (t === lightbox) close();
        });
        if (closeBtn) closeBtn.addEventListener('click', close);
        if (prevBtn)  prevBtn.addEventListener('click', function () { if (currentIndex >= 0) showAt(currentIndex - 1); });
        if (nextBtn)  nextBtn.addEventListener('click', function () { if (currentIndex >= 0) showAt(currentIndex + 1); });

        document.addEventListener('keydown', function (e) {
            if (!lightbox.classList.contains('open')) return;
            if (e.key === 'Escape')      close();
            else if (e.key === 'ArrowLeft')  showAt(currentIndex - 1);
            else if (e.key === 'ArrowRight') showAt(currentIndex + 1);
        });
    })();

    /* ─── Module 6: Star Rating ────────────────────────────────────────── */
    (function initStarRating() {
        var input = document.getElementById('ratingStarsInput');
        var wrap  = document.getElementById('starRatingPicker');
        if (!input || !wrap) return;

        var labelEl = document.getElementById('starRatingValueLabel');
        var btns    = wrap.querySelectorAll('.star-rating-btn');

        function setVal(n) {
            n = Math.max(1, Math.min(5, n));
            input.value = String(n);
            if (labelEl) labelEl.textContent = String(n);
            btns.forEach(function (b) {
                b.classList.toggle('is-on', parseInt(b.getAttribute('data-star'), 10) <= n);
            });
        }

        var initial = parseInt(input.value, 10);
        if (isNaN(initial) || initial < 1) initial = 5;
        setVal(initial);

        btns.forEach(function (btn) {
            btn.addEventListener('click', function () {
                setVal(parseInt(btn.getAttribute('data-star'), 10));
            });
        });
    })();

    /* ─── Module 7: Scroll-to-hash ─────────────────────────────────────── */
    (function initScrollToHash() {
        var hash = window.location.hash;
        if (hash !== '#ratings' && hash !== '#comments') return;
        var el = document.querySelector(hash);
        if (!el) return;
        setTimeout(function () {
            el.scrollIntoView({ behavior: 'smooth', block: 'start' });
            try { el.focus({ preventScroll: true }); } catch (e) { /* ignore */ }
        }, 80);
    })();

    /* ─── Module 8: Booking Date Calculator ────────────────────────────── */
    (function initBookingCalc() {
        var startInput = document.getElementById('startDate');
        var endInput   = document.getElementById('endDate');
        if (!startInput || !endInput) return;
        if (typeof window.__bookingPricePerNight === 'undefined') return;

        var display     = document.getElementById('totalDisplay');
        var amountInput = document.getElementById('amountHidden');
        var price       = parseFloat(window.__bookingPricePerNight) || 0;

        function recalc() {
            var s = startInput.value;
            var e = endInput.value;
            if (!s || !e) { if (display) display.value = '—'; if (amountInput) amountInput.value = '0'; return; }
            var days = Math.max(0, (new Date(e) - new Date(s)) / 86400000);
            if (days <= 0) { if (display) display.value = '—'; if (amountInput) amountInput.value = '0'; return; }
            var total = (price * days).toFixed(2);
            if (display)     display.value = '$' + parseFloat(total).toLocaleString('en-US');
            if (amountInput) amountInput.value = total;
        }

        startInput.addEventListener('change', function () {
            endInput.min = this.value;
            recalc();
        });
        endInput.addEventListener('change', recalc);
    })();

    /* ─── Module 9: Payment Card Formatter ─────────────────────────────── */
    (function initPaymentCard() {
        var cardEl   = document.getElementById('cardNumber');
        var expiryEl = document.getElementById('expiry');

        if (cardEl) {
            cardEl.addEventListener('input', function () {
                var val = this.value.replace(/\D/g, '').substring(0, 16);
                this.value = val.replace(/(.{4})/g, '$1 ').trim();
            });
        }
        if (expiryEl) {
            expiryEl.addEventListener('input', function () {
                var val = this.value.replace(/\D/g, '').substring(0, 4);
                if (val.length > 2) val = val.substring(0, 2) + '/' + val.substring(2);
                this.value = val;
            });
        }
    })();

    /* ─── Module 10: Numeric Input Filter ──────────────────────────────── */
    (function initNumericInputs() {
        document.querySelectorAll('input[inputmode="numeric"]').forEach(function (el) {
            el.addEventListener('input', function () {
                this.value = this.value.replace(/[^0-9]/g, '');
            });
        });
    })();

    /* ─── Module 11: Count-up for stat cards ───────────────────────────── */
    (function initCountUp() {
        var els = document.querySelectorAll('[data-countup]');
        if (!els.length) return;
        if (!('IntersectionObserver' in window)) return;

        function animateCount(el) {
            var target = parseInt(el.textContent, 10);
            if (isNaN(target) || target === 0) return;
            var start    = 0;
            var duration = 900;
            var startTime = null;

            function step(timestamp) {
                if (!startTime) startTime = timestamp;
                var progress = Math.min((timestamp - startTime) / duration, 1);
                var eased = 1 - Math.pow(1 - progress, 3);
                el.textContent = Math.round(start + eased * (target - start));
                if (progress < 1) requestAnimationFrame(step);
                else el.textContent = target;
            }
            requestAnimationFrame(step);
        }

        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    animateCount(entry.target);
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.3 });

        els.forEach(function (el) { observer.observe(el); });
    })();

})();
