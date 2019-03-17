

var app = app || {};

// Butterfly script from :
// http://www.michaelbromley.co.uk/blog/236/css-javascript-3d-butterfly-case-study
app.butterflyInit = function() {
	
	app.butterfly = {};
	app.butterfly.wingspan = 100;
	app.butterfly.rotationDamping = 10;
	app.butterfly.container = document.querySelector('.butterflyCon');
	app.butterfly.container.style.top = '100px';
	app.butterfly.container.style.left = '100px';
	
	$(document).click(function(e) {
		app.butterflyMoveTo(e.clientX, e.clientY);
	}); 
	
	setTimeout(function() {
		app.butterflyMoveTo(325,368);
	}, 1000);
};

app.butterflyMoveTo = function(x, y) {
	app.butterfly.container.style.visibility = 'visible';
	
    var currentX = parseInt(app.butterfly.container.style.left, 10);
    var currentY = parseInt(app.butterfly.container.style.top, 10);
    var newX = x - app.butterfly.wingspan;
    var newY = y;
    var deltaX = newX - currentX;
    var deltaY = newY - currentY;

    var rotateZ = -Math.min(Math.max(deltaX / app.butterfly.rotationDamping, -90), 90);
    var rotateX = 90 - Math.min(Math.max(deltaY / app.butterfly.rotationDamping, -90), 90);
    var translateZ = newY - 500;

    app.butterfly.container.style.left = newX + 'px';
    app.butterfly.container.style.top = newY + 'px';
    app.butterfly.container.style.transform = 'translateZ(' + translateZ + 'px) rotateX(' + rotateX + 'deg) rotateZ(' + rotateZ + 'deg)';

    clearTimeout(app.butterfly.timer);
    app.butterfly.timer = setTimeout(function() {
    	app.butterfly.container.style.transform = 'rotateX(' + rotateX + 'deg) rotateZ(0deg)';
    }, 2000);
};

app.butterflyHide = function() {
	app.butterfly.container.style.display = 'none';
}
