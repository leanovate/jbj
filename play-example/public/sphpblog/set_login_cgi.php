<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );
	
	$ok = false;
	if ( $logged_in && array_key_exists( 'user', $_POST ) && array_key_exists( 'pass', $_POST ) ) {
		$ok = create_password( sb_stripslashes( $_POST[ 'user' ] ), sb_stripslashes( $_POST[ 'pass' ] ) );
		$logged_in = $ok;
	}

	$page_title = _sb('set_login_title');
	require_once('scripts/sb_header.php');
	
	// ---------------
	// POST PROCESSING
	// ---------------
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $blog_config, $ok;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('set_login_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		if ( $ok === true ) {
			echo( _sb('set_login_success') . '<p />');
		} else {
			echo( _sb('set_login_wrong_password') . '<p />');
		}
		
		echo( '<a href="index.php">' . _sb('home') . '</a>' );
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
