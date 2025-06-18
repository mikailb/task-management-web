document.addEventListener('DOMContentLoaded', function() {
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });

    // Add fade-in animation to task cards
    const taskCards = document.querySelectorAll('.task-card');
    taskCards.forEach((card, index) => {
        card.style.animationDelay = `${index * 0.1}s`;
        card.classList.add('fade-in');
    });

    // Confirm delete
    const deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirm('Er du sikker på at du vil slette denne oppgaven?')) {
                e.preventDefault();
            }
        });
    });

    // Set minimum date to today for date inputs
    const dateInputs = document.querySelectorAll('input[type="date"]');
    const today = new Date().toISOString().split('T')[0];
    dateInputs.forEach(input => {
        if (!input.value) {
            input.value = today;
        }
        input.setAttribute('min', today);
    });

    // Task type toggle functionality
    const taskTypeInputs = document.querySelectorAll('input[name="taskType"]');
    const workFields = document.getElementById('workFields');
    const personalFields = document.getElementById('personalFields');

    function toggleTaskFields() {
        const selectedType = document.querySelector('input[name="taskType"]:checked').value;

        if (selectedType === 'work') {
            workFields.style.display = 'block';
            personalFields.style.display = 'none';
        } else {
            workFields.style.display = 'none';
            personalFields.style.display = 'block';
        }
    }

    taskTypeInputs.forEach(input => {
        input.addEventListener('change', toggleTaskFields);
    });

    // Initialize on page load
    if (taskTypeInputs.length > 0) {
        toggleTaskFields();
    }

    // Add loading state to forms
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function() {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Laster...';
                submitBtn.disabled = true;
            }
        });
    });
});