<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );
	$page_title = _sb('comment_moderation_title');

	// Create a session for the anti-spam cookie
	if ( !session_id() ) {
		session_start();
	}

	// ---------------
	// POST PROCESSING
	// ---------------
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $user_colors, $logged_in, $theme_vars, $blog_theme, $blog_config;

		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('comment_moderation_title');

		// PAGE CONTENT BEGIN
		ob_start(); ?>

		<?php echo( _sb('comment_moderation_instructions') ); ?><p />

		<?php echo( read_unmodded_comments($logged_in) ); ?><p />

		<?php
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}
	
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
