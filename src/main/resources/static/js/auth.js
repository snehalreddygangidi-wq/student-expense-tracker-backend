const loginTab = document.getElementById("loginTab");
const registerTab = document.getElementById("registerTab");
const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");

loginTab.addEventListener("click", () => {
    loginTab.classList.add("active");
    registerTab.classList.remove("active");
    loginForm.classList.remove("hidden");
    registerForm.classList.add("hidden");
});

registerTab.addEventListener("click", () => {
    registerTab.classList.add("active");
    loginTab.classList.remove("active");
    registerForm.classList.remove("hidden");
    loginForm.classList.add("hidden");
});

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = document.getElementById("loginMessage");
    message.textContent = "Logging in...";

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                email: document.getElementById("loginEmail").value,
                password: document.getElementById("loginPassword").value
            })
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem("jwt", data.token);
            localStorage.setItem("userName", data.name);
            localStorage.setItem("userEmail", data.email);
            window.location.href = "/dashboard.html";
        } else {
            message.textContent = data.message || "Login failed.";
        }
    } catch (error) {
        message.textContent = "Unable to connect to the server.";
    }
});

registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = document.getElementById("registerMessage");
    message.textContent = "Creating account...";

    try {
        const response = await fetch("/api/auth/register", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                name: document.getElementById("registerName").value,
                email: document.getElementById("registerEmail").value,
                password: document.getElementById("registerPassword").value
            })
        });

        const data = await response.json();

        if (response.ok) {
            message.textContent = "Account created. Please login.";
            registerForm.reset();
            loginTab.click();
            document.getElementById("loginEmail").value =
                document.getElementById("registerEmail").value;
        } else {
            message.textContent = data.message || "Registration failed.";
        }
    } catch (error) {
        message.textContent = "Unable to connect to the server.";
    }
});
