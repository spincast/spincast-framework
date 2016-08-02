

/*
Butterfly from :
http://www.michaelbromley.co.uk/blog/236/css-javascript-3d-butterfly-case-study
*/
var timer;
var butterflyWingspan = 100;
var rotationDamping = 10;
var container = document.querySelector('.butterflyCon');
container.style.top = '100px';
container.style.left = '100px';
document.addEventListener('click', moveTo);

function moveTo(e) {
	moveToXY(e.clientX, e.clientY);
}

function moveToXY(x, y) {
	
	container.style.visibility = 'visible';
	
    var currentX = parseInt(container.style.left, 10);
    var currentY = parseInt(container.style.top, 10);
    var newX = x - butterflyWingspan;
    var newY = y;
    var deltaX = newX - currentX;
    var deltaY = newY - currentY;

    var rotateZ = -Math.min(Math.max(deltaX / rotationDamping, -90), 90);
    var rotateX = 90 - Math.min(Math.max(deltaY / rotationDamping, -90), 90);
    var translateZ = newY - 500;

    container.style.left = newX + 'px';
    container.style.top = newY + 'px';
    container.style.transform = 'translateZ(' + translateZ + 'px) rotateX(' + rotateX + 'deg) rotateZ(' + rotateZ + 'deg)';

    clearTimeout(timer);
    timer = setTimeout(function() {
        container.style.transform = 'rotateX(' + rotateX + 'deg) rotateZ(0deg)';
    }, 2000);
}
