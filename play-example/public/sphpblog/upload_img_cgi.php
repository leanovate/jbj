<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title = _sb('upload_img_title');	
	// ---------------
	// POST PROCESSING
	// ---------------
	
	for ($i=0;$i<count($_FILES['userfile']);$i++) {
	
	if ($ok == null) {
		$ok = false;
	}
	
	if (is_uploaded_file($_FILES['userfile']['tmp_name'][$i])) {
		if ( $_FILES[ 'userfile' ][ 'error' ][$i] == 0 ) {
		if (!file_exists(IMAGES_DIR)) {
			$oldumask = umask(0);
			@mkdir(IMAGES_DIR, BLOG_MASK );
			@umask($oldumask);
		}
				
		$uploaddir = IMAGES_DIR;
		$uploadfile = $uploaddir . preg_replace("/ /","_",$_FILES['userfile']['name'][$i]);
		
		if (strpos($uploadfile, ".") === false) {
			echo('File does not have an extension');
			exit;
		}
		
		if (strpos($uploadfile, ".") == 0) {
			echo('File begins with "."');
			exit;
		}
		
		if (strrpos($uploadfile, ".") == strlen($uploadfile)-1) {
			echo('File ends with "."');
			exit;
		}
		
		$extension = strtolower(substr(strrchr($uploadfile, "."), 1));
		
		if (strlen($extension) == 0) { // Not really needed...
			echo('File ends with "." and does not have an extension');
			exit;
		}
		
		// Allowed files
		$upload_valid_extentions = array( "jpg", "gif", "png" );
		$extension = strtolower(substr(strrchr($uploadfile, "."), 1));
		if (!in_array($extension, $upload_valid_extentions)) {
			echo('That filetype is not allowed');
			exit;
		}
		
		if ( move_uploaded_file($_FILES['userfile']['tmp_name'][$i], $uploadfile ) ) {
			chmod( $uploadfile, BLOG_MASK );
			$ok = true;
		} else {
			$ok = false;
		}

		}
	}
	}
	if ( $ok === true ) {
	redirect_to_url( 'index.php' );
	}


	require_once('scripts/sb_header.php');

	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
	global $user_colors;
	if ( $ok !== true ) {
 		switch($_FILES['userfile'][$i]['error'])
 		{
 			case 0: $errstring = _sb("There is no error, the file uploaded with success."); break;
 			case 1: $errstring = _sb("Value: 1; The uploaded file exceeds the upload_max_filesize directive in php.ini."); break;
 			case 2: $errstring = _sb("The uploaded file exceeds the MAX_FILE_SIZE directive that was specified in the HTML form."); break;
 			case 3: $errstring = _sb("The uploaded file was only partially uploaded."); break;
 			case 4: $errstring = _sb("No file was uploaded."); break;
 			case 6: $errstring = _sb("Missing a temporary folder. Introduced in PHP 4.3.10 and PHP 5.0.3."); break;
 			case 7: $errstring = _sb("Failed to write file to disk. Introduced in PHP 5.1.0."); break;
 			case 8: $errstring = _sb("File upload stopped by extension. Introduced in PHP 5.2.0."); break;
 			default: $errstring = _sb("The reason of error is unknown.") . "<br>" . printf(_sb("Please look at the %s error code for the _FILES['userfile']['error'] in the php manual."), $_FILES['userfile'][$i]['error']); break;
 		}
 		echo( _sb('upload_img_error') . $ok . $errstring . '<p />');
	}
	//echo(count($_FILES['userfile']));
	//print_r($_FILES['userfile']);
	//echo($_FILES['userfile']['name'][0]);
	echo( '<a href="index.php">' . _sb('home') . '</a><br /><br />' );
	}
	
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
