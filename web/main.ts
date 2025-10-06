
const staticBase64Image = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAAXNSR0IArs4c6QAAABNJREFUGFdjZGBg/M/A8J+BgYABAAHzA0gA/b/tAAAAAElFTkSuQmCC";
const imageElement = document.getElementById('frame') as HTMLImageElement;
const statsElement = document.getElementById('stats') as HTMLElement;

if (imageElement && statsElement) {
  // Set the image source to our static Base64 image
  imageElement.src = staticBase64Image;

  // Update the text content of the stats paragraph
  statsElement.textContent = "FPS: 15 | Resolution: 640x480";
}
