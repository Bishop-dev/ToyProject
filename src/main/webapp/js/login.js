$(function() {
	$('#loginForm').on('submit', function() {
		return validateLoginAction();
	});
});

function validateLoginAction() {
	$('.message').empty().hide();
	var login = $('#login').val();
	var password = $('#password').val();
	var result = (login != "" && password != "");
	if (!result) {
		$('.message').text('Enter login and password');
		$('.message').css('display', 'block');
		$('.message').css('background', 'pink');
	}
	return result;
}