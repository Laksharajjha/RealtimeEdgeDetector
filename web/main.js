// This is a Base64 representation of a simple black and white checkerboard image.
// It acts as our "dummy processed frame".
var staticBase64Image = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAAXNSR0IArs4c6QAAABNJREFUGFdjZGBg/M/A8J+BgYABAAHzA0gA/b/tAAAAAElFTkSuQmCC";
// Get the HTML elements by their ID
var imageElement = document.getElementById('frame');
var statsElement = document.getElementById('stats');
// Check if the elements were found before using them
if (imageElement && statsElement) {
    // Set the image source to our static Base64 image
    imageElement.src = staticBase64Image;
    // Update the text content of the stats paragraph
    statsElement.textContent = "FPS: 15 | Resolution: 640x480";
}
