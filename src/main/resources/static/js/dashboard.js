/* Dashboard Interactivity — BBVA Banking Dashboard */
/* MBA-1811: Design Specification */

document.addEventListener('DOMContentLoaded', function () {
    initNotificationBell();
    initTransactionFilters();
    initQuickTransfer();
    initToast();
});

/* ========== Notification Bell & Dropdown ========== */
function initNotificationBell() {
    var bell = document.getElementById('notificationBell');
    var dropdown = document.getElementById('notificationDropdown');
    if (!bell || !dropdown) return;

    bell.addEventListener('click', function (e) {
        e.stopPropagation();
        dropdown.classList.toggle('notification-dropdown--open');
    });

    document.addEventListener('click', function (e) {
        if (!dropdown.contains(e.target)) {
            dropdown.classList.remove('notification-dropdown--open');
        }
    });

    var markReadBtn = document.getElementById('markAllRead');
    if (markReadBtn) {
        markReadBtn.addEventListener('click', function () {
            var items = dropdown.querySelectorAll('.notification-item--unread');
            items.forEach(function (item) {
                item.classList.remove('notification-item--unread');
            });
            var badge = bell.querySelector('.notification-bell__badge');
            if (badge) badge.style.display = 'none';
        });
    }
}

/* ========== Transaction Filters ========== */
function initTransactionFilters() {
    var typeFilter = document.getElementById('filterType');
    var dateFromFilter = document.getElementById('filterDateFrom');
    var dateToFilter = document.getElementById('filterDateTo');
    var accountFilter = document.getElementById('filterAccount');
    var table = document.getElementById('transactionsTable');
    if (!table) return;

    function applyFilters() {
        var rows = table.querySelectorAll('tbody tr');
        var type = typeFilter ? typeFilter.value : '';
        var dateFrom = dateFromFilter ? dateFromFilter.value : '';
        var dateTo = dateToFilter ? dateToFilter.value : '';
        var account = accountFilter ? accountFilter.value : '';

        rows.forEach(function (row) {
            var rowType = row.getAttribute('data-tx-type') || '';
            var rowDate = row.getAttribute('data-tx-date') || '';
            var rowAccount = row.getAttribute('data-account') || '';
            var visible = true;

            if (type && rowType !== type) visible = false;
            if (dateFrom && rowDate < dateFrom) visible = false;
            if (dateTo && rowDate > dateTo) visible = false;
            if (account && rowAccount !== account) visible = false;

            row.style.display = visible ? '' : 'none';
        });
    }

    [typeFilter, dateFromFilter, dateToFilter, accountFilter].forEach(function (el) {
        if (el) el.addEventListener('change', applyFilters);
    });
}

/* ========== Quick Transfer ========== */
function initQuickTransfer() {
    var form = document.getElementById('quickTransferForm');
    var confirmModal = document.getElementById('transferConfirmModal');
    var confirmBtn = document.getElementById('confirmTransferBtn');
    var cancelBtn = document.getElementById('cancelTransferBtn');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        e.preventDefault();
        if (!validateTransferForm()) return;

        var fromAccount = document.getElementById('fromAccount');
        var toAccount = document.getElementById('toAccount');
        var amount = document.getElementById('transferAmount');

        document.getElementById('confirmFrom').textContent =
            fromAccount.options[fromAccount.selectedIndex].text;
        document.getElementById('confirmTo').textContent =
            toAccount.options[toAccount.selectedIndex].text;
        document.getElementById('confirmAmount').textContent =
            '$' + parseFloat(amount.value).toLocaleString('en-US', {minimumFractionDigits: 2});

        if (confirmModal) {
            confirmModal.classList.add('modal-overlay--open');
        }
    });

    if (cancelBtn) {
        cancelBtn.addEventListener('click', function () {
            confirmModal.classList.remove('modal-overlay--open');
        });
    }

    if (confirmBtn && confirmModal) {
        confirmBtn.addEventListener('click', function () {
            confirmModal.classList.remove('modal-overlay--open');
            executeTransfer();
        });
    }
}

function validateTransferForm() {
    var valid = true;
    var fromAccount = document.getElementById('fromAccount');
    var toAccount = document.getElementById('toAccount');
    var amount = document.getElementById('transferAmount');

    clearErrors();

    if (!fromAccount.value) {
        showFieldError('fromAccount', 'Please select a source account');
        valid = false;
    }
    if (!toAccount.value) {
        showFieldError('toAccount', 'Please select a destination account');
        valid = false;
    }
    if (fromAccount.value && toAccount.value && fromAccount.value === toAccount.value) {
        showFieldError('toAccount', 'Source and destination must be different');
        valid = false;
    }
    if (!amount.value || parseFloat(amount.value) <= 0) {
        showFieldError('transferAmount', 'Please enter a valid amount');
        valid = false;
    }
    return valid;
}

function showFieldError(fieldId, message) {
    var field = document.getElementById(fieldId);
    if (!field) return;
    field.classList.add('form-group__input--error');
    var errorEl = field.parentElement.querySelector('.form-group__error');
    if (errorEl) {
        errorEl.textContent = message;
        errorEl.classList.add('form-group__error--visible');
    }
}

function clearErrors() {
    document.querySelectorAll('.form-group__input--error').forEach(function (el) {
        el.classList.remove('form-group__input--error');
    });
    document.querySelectorAll('.form-group__error--visible').forEach(function (el) {
        el.classList.remove('form-group__error--visible');
    });
}

function executeTransfer() {
    var fromAccount = document.getElementById('fromAccount').value;
    var toAccount = document.getElementById('toAccount').value;
    var amount = document.getElementById('transferAmount').value;

    var contextPath = document.querySelector('meta[name="context-path"]');
    var basePath = contextPath ? contextPath.getAttribute('content') : '/bank-api';

    fetch(basePath + '/dashboard/transfer', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            fromAccountNumber: parseInt(fromAccount),
            toAccountNumber: parseInt(toAccount),
            amount: parseFloat(amount)
        })
    })
    .then(function (response) {
        return response.json().then(function (data) {
            return {ok: response.ok, data: data};
        });
    })
    .then(function (result) {
        if (result.ok) {
            showToast('success', result.data.message || 'Transfer completed successfully!');
            setTimeout(function () { location.reload(); }, 1500);
        } else {
            showToast('error', result.data.message || 'Transfer failed. Please try again.');
        }
    })
    .catch(function () {
        showToast('error', 'Network error. Please check your connection.');
    });
}

/* ========== Toast Notifications ========== */
var toastContainer;

function initToast() {
    toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.className = 'toast-container';
        document.body.appendChild(toastContainer);
    }
}

function showToast(type, message) {
    if (!toastContainer) initToast();

    var icons = {
        success: '&#10003;',
        error: '&#10007;',
        warning: '&#9888;',
        info: '&#8505;'
    };

    var toast = document.createElement('div');
    toast.className = 'toast toast--' + type;
    var iconSpan = document.createElement('span');
    iconSpan.className = 'toast__icon';
    iconSpan.innerHTML = icons[type] || '';
    var msgSpan = document.createElement('span');
    msgSpan.className = 'toast__message';
    msgSpan.textContent = message;
    var closeBtn = document.createElement('button');
    closeBtn.className = 'toast__close';
    closeBtn.innerHTML = '&times;';
    closeBtn.onclick = function() { toast.remove(); };
    toast.appendChild(iconSpan);
    toast.appendChild(msgSpan);
    toast.appendChild(closeBtn);

    toastContainer.appendChild(toast);

    setTimeout(function () {
        toast.style.animation = 'slideOut 300ms ease forwards';
        setTimeout(function () { toast.remove(); }, 300);
    }, 5000);
}
