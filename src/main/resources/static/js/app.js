(() => {
    const formatter = new Intl.NumberFormat('en-US');

    function digitsOnly(value) {
        return String(value || '').replace(/[^\d]/g, '');
    }

    document.querySelectorAll('form').forEach((form) => {
        form.addEventListener('submit', () => {
            form.querySelectorAll('.money-input').forEach((input) => {
                input.value = digitsOnly(input.value);
            });
            const submitter = form.querySelector('[type="submit"]');
            if (submitter) {
                submitter.classList.add('loading-lock');
                submitter.dataset.originalText = submitter.textContent;
                submitter.textContent = submitter.dataset.loadingText || 'Đang xử lý...';
                submitter.disabled = true;
            }
        });
    });

    document.querySelectorAll('a[data-loading-text], button[data-loading-text]').forEach((button) => {
        button.addEventListener('click', () => {
            button.classList.add('loading-lock');
            button.dataset.originalText = button.textContent;
            button.textContent = button.dataset.loadingText || 'Đang xử lý...';
        });
    });

    document.querySelectorAll('.money-input').forEach((input) => {
        const render = () => {
            const raw = digitsOnly(input.value);
            input.value = raw ? formatter.format(Number(raw)) : '';
        };
        render();
        input.addEventListener('input', render);
    });
})();
