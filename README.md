# ImageHashing

Image hashing program that will reduce the size of an image, gather the average grayscale value and the provide a hashcode based on comapring a selected pixels greyscale value compared to the average of the image. This is done for 64 pixels and provides a string with a length of 64 chars of 1's and 0's giving the image it's hashcode. All hashcodes are stored in a text file. Then a hamming distance function is applied to find all similar images to an inputed image.


The database of images used is folder of 120,000 images. Hashtime takes around 3 minutes total. Search time takes less than a second.
