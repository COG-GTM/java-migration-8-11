document.addEventListener('DOMContentLoaded', function () {

    /* ---- Transaction filter ---- */
    var searchInput = document.getElementById('txSearch');
    var typeFilter = document.getElementById('txTypeFilter');
    if (searchInput && typeFilter) {
        function filterTransactions() {
            var query = searchInput.value.toLowerCase();
            var type = typeFilter.value;
            var items = document.querySelectorAll('.transaction-item');
            items.forEach(function (item) {
                var desc = (item.getAttribute('data-description') || '').toLowerCase();
                var txType = item.getAttribute('data-type') || '';
                var matchesQuery = !query || desc.indexOf(query) !== -1;
                var matchesType = !type || txType === type;
                item.style.display = (matchesQuery && matchesType) ? '' : 'none';
            });
        }
        searchInput.addEventListener('input', filterTransactions);
        typeFilter.addEventListener('change', filterTransactions);
    }

    /* ---- Transfer form ---- */
    var transferForm = document.getElementById('transferForm');
    if (transferForm) {
        transferForm.addEventListener('submit', function (e) {
            e.preventDefault();
            var fromAcct = document.getElementById('fromAccount').value;
            var toAcct = document.getElementById('toAccount').value;
            var amount = document.getElementById('transferAmount').value;
            var csrfToken = document.querySelector('input[name="_csrf"]');

            if (!fromAcct || !toAcct || !amount || parseFloat(amount) <= 0) {
                showTransferMessage('Please fill in all fields with valid values.', true);
                return;
            }
            if (fromAcct === toAcct) {
                showTransferMessage('Source and destination accounts must be different.', true);
                return;
            }

            var submitBtn = transferForm.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.textContent = 'Processing...';

            var headers = { 'Content-Type': 'application/json' };
            if (csrfToken) {
                headers['X-CSRF-TOKEN'] = csrfToken.value;
            }

            fetch('/bank-api/dashboard/transfer', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify({
                    fromAccountNumber: parseInt(fromAcct),
                    toAccountNumber: parseInt(toAcct),
                    transferAmount: parseFloat(amount)
                })
            })
            .then(function (response) { return response.json(); })
            .then(function (data) {
                if (data.success) {
                    showTransferMessage('Transfer completed successfully!', false);
                    setTimeout(function () { window.location.reload(); }, 1500);
                } else {
                    showTransferMessage(data.message || 'Transfer failed.', true);
                }
            })
            .catch(function () {
                showTransferMessage('An error occurred. Please try again.', true);
            })
            .finally(function () {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Transfer Funds';
            });
        });
    }

    function showTransferMessage(message, isError) {
        var msgEl = document.getElementById('transferMessage');
        if (msgEl) {
            msgEl.textContent = message;
            msgEl.className = isError ? 'transfer-error' : 'transfer-success';
            msgEl.style.display = 'block';
        }
    }

    /* ---- Notification panel toggle ---- */
    var notifBtn = document.getElementById('notificationToggle');
    var notifPanel = document.getElementById('notificationPanel');
    if (notifBtn && notifPanel) {
        notifBtn.addEventListener('click', function () {
            notifPanel.scrollIntoView({ behavior: 'smooth' });
        });
    }
});
