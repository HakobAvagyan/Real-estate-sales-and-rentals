(function () {
    'use strict';

    function escapeHtml(s) {
        if (!s) {
            return '';
        }
        return String(s).replace(/[&<>"']/g, function (c) {
            return {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[c];
        });
    }

    var cfg = document.getElementById('chat-page-config');
    if (!cfg) {
        return;
    }

    var selectedId = parseInt(cfg.getAttribute('data-selected-conversation-id'), 10);
    var currentUserId = parseInt(cfg.getAttribute('data-current-user-id'), 10);
    if (!selectedId || Number.isNaN(selectedId)) {
        return;
    }
    if (Number.isNaN(currentUserId)) {
        currentUserId = 0;
    }

    if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
        return;
    }

    var socket = new SockJS('/ws-chat');
    var client = Stomp.over(socket);
    client.debug = null;
    client.connect({}, function () {
        client.subscribe('/user/queue/chat-messages', function (msg) {
            var body = JSON.parse(msg.body);
            if (body.conversationId !== selectedId) {
                return;
            }
            var list = document.getElementById('msg-list');
            if (!list) {
                return;
            }
            var row = document.createElement('div');
            row.className = 'chat-bubble-row' + (body.senderId === currentUserId ? ' mine' : '');
            row.innerHTML = '<div class="chat-bubble"><div class="chat-meta">'
                + escapeHtml(body.senderName) + ' · ' + escapeHtml(String(body.createdAt)) + '</div>'
                + '<div class="chat-text">' + escapeHtml(body.text) + '</div></div>';
            list.appendChild(row);
            list.scrollTop = list.scrollHeight;
        });
    });
})();
