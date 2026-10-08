const money = (value) =>
    new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2
    }).format(value);

function authHeaders(extra = {}) {
    const token = localStorage.getItem("jwt");
    return {
        ...extra,
        ...(token ? {"Authorization": `Bearer ${token}`} : {})
    };
}

function clearAuthAndRedirect() {
    localStorage.removeItem("jwt");
    localStorage.removeItem("userName");
    localStorage.removeItem("userEmail");
    window.location.href = "/index.html";
}

async function checkLogin() {
    const response = await fetch("/api/auth/me", {
        headers: authHeaders()
    });

    if (!response.ok) {
        clearAuthAndRedirect();
        return null;
    }

    const user = await response.json();
    document.getElementById("welcomeName").textContent = `Hi, ${user.name}`;
    return user;
}

async function loadDashboard() {
    const response = await fetch("/api/dashboard", {
        headers: authHeaders()
    });

    if (response.status === 401) {
        clearAuthAndRedirect();
        return;
    }

    const data = await response.json();

    document.getElementById("totalAmount").textContent = money(data.total);
    document.getElementById("monthlyAmount").textContent = money(data.monthlyTotal);
    document.getElementById("expenseCount").textContent = data.expenseCount;

    const categoryList = document.getElementById("categoryList");
    const entries = Object.entries(data.categoryTotals);

    if (entries.length === 0) {
        categoryList.innerHTML = '<p class="muted">No expenses yet.</p>';
        return;
    }

    categoryList.innerHTML = entries.map(([category, amount]) => `
        <div class="category-row">
            <span>${escapeHtml(category)}</span>
            <strong>${money(amount)}</strong>
        </div>
    `).join("");
}

async function loadExpenses() {
    const response = await fetch("/api/expenses", {
        headers: authHeaders()
    });

    if (response.status === 401) {
        clearAuthAndRedirect();
        return;
    }

    const expenses = await response.json();
    const table = document.getElementById("expenseTable");
    const empty = document.getElementById("emptyMessage");

    table.innerHTML = "";

    if (expenses.length === 0) {
        empty.style.display = "block";
        return;
    }

    empty.style.display = "none";

    expenses.forEach(expense => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${escapeHtml(expense.expenseDate)}</td>
            <td>${escapeHtml(expense.title)}</td>
            <td>${escapeHtml(expense.category)}</td>
            <td>${escapeHtml(expense.description || "-")}</td>
            <td><strong>${money(expense.amount)}</strong></td>
            <td>
                <button class="delete-btn" onclick="deleteExpense(${expense.id})">
                    Delete
                </button>
            </td>
        `;

        table.appendChild(row);
    });
}

async function deleteExpense(id) {
    if (!confirm("Delete this expense?")) {
        return;
    }

    const response = await fetch(`/api/expenses/${id}`, {
        method: "DELETE",
        headers: authHeaders()
    });

    if (response.ok) {
        await refreshPage();
    } else if (response.status === 401) {
        clearAuthAndRedirect();
    } else {
        const data = await response.json();
        alert(data.message || "Could not delete expense.");
    }
}

document.getElementById("expenseForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = document.getElementById("expenseMessage");
    message.textContent = "Saving...";

    const response = await fetch("/api/expenses", {
        method: "POST",
        headers: authHeaders({"Content-Type": "application/json"}),
        body: JSON.stringify({
            title: document.getElementById("title").value,
            amount: Number(document.getElementById("amount").value),
            category: document.getElementById("category").value,
            expenseDate: document.getElementById("expenseDate").value,
            description: document.getElementById("description").value
        })
    });

    const data = await response.json();

    if (response.ok) {
        message.textContent = "Expense added successfully.";
        document.getElementById("expenseForm").reset();
        document.getElementById("expenseDate").value = today();
        await refreshPage();
    } else if (response.status === 401) {
        clearAuthAndRedirect();
    } else {
        message.textContent = data.message || "Could not add expense.";
    }
});

document.getElementById("logoutBtn").addEventListener("click", async () => {
    await fetch("/api/auth/logout", {
        method: "POST",
        headers: authHeaders()
    });

    clearAuthAndRedirect();
});

document.getElementById("refreshBtn").addEventListener("click", refreshPage);

function today() {
    return new Date().toISOString().split("T")[0];
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

async function refreshPage() {
    await Promise.all([loadDashboard(), loadExpenses()]);
}

(async function start() {
    const user = await checkLogin();

    if (user) {
        document.getElementById("expenseDate").value = today();
        await refreshPage();
    }
})();
