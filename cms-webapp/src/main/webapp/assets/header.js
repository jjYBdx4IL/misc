var didScroll;
var lastScrollTop = 0;
var delta = 5;
var navbarHeight = $('header').outerHeight();
var headerState = 1;

$(window).scroll(function(event) {
	didScroll = true;
});

setInterval(function() {
	if (didScroll) {
		hasScrolled();
		didScroll = false;
	}
}, 250);

function hasScrolled() {
	var st = $(this).scrollTop();

	if (headerState == 0 && st < delta) {
		$('header').removeClass('nav-up').addClass('nav-down');
		headerState = 1;
		lastScrollTop = st;
		return;
	}

	// Make sure they scroll more than delta
	if (Math.abs(lastScrollTop - st) <= delta)
		return;

	// If they scrolled down and are past the navbar, add class .nav-up.
	// This is necessary so you never see what is "behind" the navbar.
	if (headerState == 1 && st > lastScrollTop && st > navbarHeight) {
		// Scroll Down
		$('header').removeClass('nav-down').addClass('nav-up');
		headerState = 0;
		lastScrollTop = st;
	}
}

/* trivial drop-down menu: it's always below the header bar, but only when the user
 * click on the menu icon the header bar gets extended downwards and reveals the menu's
 * contents.
 */
$(document).ready(function() {
	var rememberedHeight = $("header").css("height");
	var state = 0;
	$(".menuIcon").click(function() {
		var height = $("header").css("height");
		if (state == 1) {
			$("header").css("height", rememberedHeight);
			state = 0;
		} else {
			$("header").css("height", "auto");
			state = 1;
		}
	});
});
