<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( false, true );

	// TODO dynamically grab this
	//$page_title = 'static_title';
	
	// ---------------
	// POST PROCESSING
	// ---------------
	
	$redirect = true;
	if ( array_key_exists( 'page', $_GET ) ) {		
		$redirect = false;
	}
	
	if ( $redirect === true ) {
		redirect_to_url( 'index.php' );
	}
	
	global $entry_array;
	$static_page = urldecode( $_GET[ 'page' ] );
	$static_page = preg_replace( '/(\s|\\\|\/|%|#)/', '_', $static_page );
	$entry_array = read_static_entry( $static_page, $logged_in );
	
	// ------------
	// PAGE CONTENT
	// ------------

	require_once('scripts/sb_header.php');

	function page_content() {
		global $logged_in, $entry_array;
		
		echo( theme_staticentry( $entry_array, $logged_in ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
