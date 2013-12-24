<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );
	$page_title = _sb('comments_title');

	if ( !session_id() ) {
		session_start();
	}

	// ---------------
	// POST PROCESSING
	// ---------------

	// Verify information being passed
	global $ok;
	$ok = true;
	if (isset( $_GET[ "comment" ] ) ) {
		// Get the filename of the comment file to be approved
		$filename = CONTENT_DIR . $_GET[ 'y' ] . '/' . $_GET[ 'm' ] . '/' . sb_strip_extension( $_GET[ "entry" ] ) . '/comments/' . $_GET[ "comment" ];
		$ok = set_comment_holdflag( $filename, '');
	} else {
		$ok = _sb('Error! Comment cannot be approved. Unknown error.');
	}

	if ( $ok == true ) {
		redirect_to_url( 'comments_moderation.php' );
	}
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $blog_config, $ok;
	
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('comments_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		if ( $ok !== true ) {
			echo _sb('error_ban') . $ok . '<p />';
		} else {
			echo _sb('success_ban1') . '(' . $_GET[ "ban" ] . ')' . _sb('success_ban2') . '<p />';
		}
		echo( '<a href="index.php">' . _sb('home') . '</a>' );
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
