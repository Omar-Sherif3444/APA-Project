document.addEventListener('DOMContentLoaded', function () {
    const dateCardsContainer = document.querySelector('.Date .All');
    const timeAnchors = document.querySelectorAll('.buttons-showtime a');
    const movieTitleElement = document.querySelector('.Title h2');
    const movieTitle = movieTitleElement ? movieTitleElement.textContent.trim() : null;

    if (!dateCardsContainer || !timeAnchors.length || !movieTitle) {
        console.warn('Missing date container, time anchors, or movie title');
        return;
    }

    const style = document.createElement('style');
    style.textContent = `
        .date-card {
            background-color: rgba(0, 0, 0, 0.159);
            text-align: center;
            padding: 15px;
            cursor: pointer;
            transition: box-shadow 0.5s ease;
            margin-left: 10px;
            user-select: none;
        }
        .date-card:first-child {
            margin-left: 0;
        }
        .date-card:hover {
            border-radius: 10px;
            border: 3px solid #C22D7B;
            color: #C22D7B;
            box-shadow: 0px 0px 20px #6C6C70;
        }
        .selected-day {
            border-radius: 10px;
            border: 3px solid #C22D7B !important;
            color: #C22D7B;
            box-shadow: 0px 0px 20px #6C6C70;
        }
    `;
    document.head.appendChild(style);

    const dayNames = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                        'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    const today = new Date();

    function formatDate(date) {
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}-${month}-${year}`;
    }

    function getDateLabel(index) {
        if (index === 0) return 'Today';
        if (index === 1) return 'Tomorrow';
        return null;
    }

    function createDateCard(date, index) {
        const card = document.createElement('div');
        card.className = 'date-card';

        const dayName = dayNames[date.getDay()];
        const label = getDateLabel(index);
        const dateStr = formatDate(date);

        const dayP = document.createElement('p');
        dayP.textContent = dayName;

        const labelH4 = document.createElement('h4');
        if (label) {
            labelH4.textContent = label;
        } else {
            labelH4.textContent = `${date.getDate()} ${monthNames[date.getMonth()]}`;
        }

        const dateP = document.createElement('p');
        dateP.textContent = dateStr;

        card.appendChild(dayP);
        card.appendChild(labelH4);
        card.appendChild(dateP);

        card.dataset.showDate = dateStr;

        return card;
    }

    dateCardsContainer.innerHTML = '';
    const dateCards = [];

    for (let i = 0; i < 6; i++) {
        const date = new Date(today);
        date.setDate(today.getDate() + i);
        const card = createDateCard(date, i);
        dateCardsContainer.appendChild(card);
        dateCards.push(card);
    }

    function updateTimeLinks(selectedDate) {
        timeAnchors.forEach(anchor => {
            const timeInput = anchor.querySelector('input');
            const time = anchor.dataset.time || (timeInput ? timeInput.value.trim() : null);
            const showId = anchor.dataset.showId;
            const params = new URLSearchParams();

            if (showId) {
                params.set('showId', showId);
            }
            if (movieTitle) {
                params.set('movieTitle', movieTitle);
            }
            if (selectedDate) {
                params.set('showDate', selectedDate);
            }
            if (time) {
                params.set('showTime', time);
            }

            anchor.setAttribute('href', '/Seats?' + params.toString());
        });
    }

    function selectCard(card) {
        dateCards.forEach(c => c.classList.remove('selected-day'));
        card.classList.add('selected-day');
        const selectedDate = card.dataset.showDate;
        if (selectedDate) {
            updateTimeLinks(selectedDate);
        }
    }

    dateCards.forEach(card => {
        card.addEventListener('click', () => selectCard(card));
    });

    if (dateCards.length > 0) {
        selectCard(dateCards[0]);
    }
});