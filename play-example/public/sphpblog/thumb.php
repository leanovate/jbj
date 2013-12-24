<?php
require_once('scripts/sb_functions.php');

// ensure there is no funny business in the $file path like ..
$file = basename($_GET['file']);

$ifile = IMAGES_DIR.$file;

//echo $ifile;

// detect file type first
$meta = pathinfo($ifile);
$ext = strtolower($meta['extension']);

$img_origineel = NULL;
if ($ext == 'jpg' or $ext == 'jpeg') {
    $img_origineel = imagecreatefromjpeg($ifile);
}
if ($ext == 'gif') {
    $img_origineel = imagecreatefromgif($ifile);
}
if ($ext == 'png') {
    $img_origineel = imagecreatefrompng($ifile);
}

if ($img_origineel) {

    $new_w = imagesx($img_origineel);
    $new_h = imagesy($img_origineel);

    header("Content-type: image/jpeg");

    if ($new_h > $new_w)
	{
	$height = "100";
	$new_w = abs($new_w / ($new_h / $height));
	$new_h = $height;
	$tussen = abs((100 - $new_w)/2);
	$img_destination = imagecreatetruecolor(100 ,100);
	imagecopyresampled($img_destination,$img_origineel,$tussen,0,0,0,$new_w,$new_h,imagesx($img_origineel),imagesy($img_origineel));
	imageJPEG($img_destination);

	}
    else
	{
	$width = "100";
	$new_h = abs($new_h / ($new_w / $width));
	$new_w = $width;
	$tussen = abs((100 - $new_h)/2);
	$img_destination = imagecreatetruecolor(100 ,100);
	imagecopyresampled($img_destination,$img_origineel,0,$tussen,0,0,$new_w,$new_h,imagesx($img_origineel),imagesy($img_origineel));
	imageJPEG($img_destination);
	}
    ImageDestroy($img_destination); 

}

?>
