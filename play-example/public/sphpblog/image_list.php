<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true ); 
	$page_title = _sb('image_list_title');

	// ---------------
	// POST PROCESSING
	// ---------------
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $blog_config;
	
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('image_list_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		echo( _sb('image_list_instructions') . '<p /><hr />');
		echo image_list();
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}
	require_once(ROOT_DIR . '/scripts/sb_footer.php');

?>
