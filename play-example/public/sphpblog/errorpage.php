<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( false, true );
	if ( !session_id() ) {
		session_start();
	}
	$page_title = _sb('errorpage_title');

	// ---------------
	// POST PROCESSING
	// ---------------

	// ------------
	// PAGE CONTENT
	// ------------

	require_once('scripts/sb_header.php');
	function page_content() {
		global $blog_config, $blog_theme;

		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('errorpage_title');
		$entry_array[ 'entry' ] = '<table width="100%"	border="0"><tr><td><img src="themes/' . $blog_theme . '/images/error_icon.png" alt="" border="0" /></td>';
		if (isset($_SESSION['errortype'])) {
			$entry_array[ 'entry' ]	 .= '<td>' . $_SESSION['errornum'] . ' ' . $_SESSION['errortype'] . '<br><br>';
		} else {
			$entry_array[ 'entry' ]  .= '<td>' . _sb('errorline1') . '<br><br>';
			$entry_array[ 'entry' ]  .= _sb('errorline2') . '<br><br>';
		}

		$entry_array[ 'entry' ]	 .= _sb('clientid') . @gethostbyaddr(getIP()) . '</td></tr></table>';

		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
