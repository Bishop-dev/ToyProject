$(function () {

    $('#registrationForm').on('submit', function () {
        return validateRegistrationForm();
    });

    $("#datepicker").datepicker();

    $("#login").on('blur', function () {
        checkLogin();
    });

});

function validateRegistrationForm() {
    $('.message').empty();
    if ($('#login').val() === "") {
        setMessage('Login is empty');
    }
    validatePasswords();
    validateEmail();
    if ($('#firstName').val() === "") {
        setMessage("First Name is empty");
    }
    if ($('#lastName').val() === "") {
        setMessage("Last Name is empty");
    }
    validateDate();
    if ($('#recaptcha_response_field').val() === "") {
        setMessage("Captcha is empty");
    }
    if (!($('.message').text() === "")) {
        $('.message').css('display', 'block');
        $('.message').css('background', 'pink');
    }
    return $('.message').text() === "";
}

function checkLogin() {
    $.ajax({
        url: "check",
        type: "GET",
        data: {
            login: $("#login").val()
        },
        dataType: "text"
    }).success(function (answer) {
            if (answer === "true") {
                $("#loginAnswer").text('Login free');
                $("#loginAnswer").css('background', 'lime');
                $("#createSubmit").removeAttr('disabled');
            } else {
                $("#loginAnswer").text('Login busy');
                $("#loginAnswer").css('background', 'pink');
                $("#createSubmit").attr('disabled', 'disabled');
            }
            $("#loginAnswer").fadeIn("slow");
        });
}

function validatePasswords() {
    var password = $('#password').val();
    var confirm = $('#confirm').val();
    if (password === "" || confirm === "") {
        setMessage('Passwords empty');
    }
    if (password != confirm) {
        setMessage('Passwords do not match');
    }
}

function validateEmail() {
    var email = $('#email').val();
    var expression = new RegExp('\\w{1,100}@\\w{1,100}\.\\w{2,3}', 'g');
    if (!email.match(expression)) {
        setMessage('Wrong email');
    }
}

function validateDate() {
    var birthday = $('#datepicker').val();
    var expression = new RegExp('\\d{2}/\\d{2}/\\d{4}', 'g');
    if (!birthday.match(expression)) {
        setMessage('Birthday is empty');
    }
}

function setMessage(message) {
    $('.message').append(message + "<br>");
}