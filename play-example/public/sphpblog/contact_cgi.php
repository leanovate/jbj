<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( false, true );
	$page_title = _sb('contact_title');
	
	// -------------
	// POST PROCESSING
	// -------------
	if ( !session_id() ) {
		session_start();
	}
	
	$client_ip_local = getIP();
	
		if (!isset($_SESSION['cookies_enabled'])) {
		redirect_to_url('errorpage.php');
		// header('location: http://'.$_SERVER['HTTP_HOST'].dirname($_SERVER['PHP_SELF']).'errorpage-nocookies.php');
	}
	
	$comment_date = time();
	
	$subject=_sb('contactsent') . $blog_config->getTag('BLOG_TITLE');
	$body='<b>' . _sb('name') . '</b> ' . $_POST[ 'name' ] . '<br />';
	$body .= '<b>' . _sb('IPAddress') . '</b> ' . $client_ip_local . ' (' . @gethostbyaddr($client_ip_local) .')<br />';
	$body .= '<b>' . _sb('useragent') . '</b> ' . $_SERVER[ 'HTTP_USER_AGENT' ] . '<br />';
	$body .= '<b>' . _sb('email') . '</b> ' . $_POST[ 'email' ] . '<br />';
	$body .= '<b>' . _sb('subject') . '</b> ' . $_POST[ 'subject' ] . '<br /><br />';
	$body .= '<b>' . _sb('comment') . '</b><br /><br />';
	$body .= sprintf( _sb('wrote'), format_date( $comment_date ), $_POST[ 'name' ], str_replace( "\r\n", "<br />\r\n", $_POST[ 'comment' ] ) );
	$ok=false;
	if ($_POST[ 'capcha_contact' ] == $_SESSION[ 'capcha_contact' ] AND $_SESSION[ 'capcha_contact' ] != '' ) {
		$ok=sb_mail( $_POST[ 'email' ], $blog_config->getTag('BLOG_EMAIL'), $subject, $body, false );
	} else {
		$_SESSION['errornum'] = '403.8';
		$_SESSION['errortype'] = _sb('error_emailnotsentcapcha');
		redirect_to_url('errorpage.php');
		// header('location: http://'.$_SERVER['HTTP_HOST'].dirname($_SERVER['PHP_SELF']).'errorpage.php');
	}
	@session_unregister( 'capcha_contact' );
	
	// -----------
	// PAGE CONTENT
	// -----------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $blog_config, $ok;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('contact_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		if ( $ok == true ) { 
			echo( _sb('contact_success') );
		} else {
			$_SESSION['errornum'] = '403.8';
			$_SESSION['errortype'] = _sb('error_emailnotsent');
			redirect_to_url('errorpage.php');
			// header('location: http://'.$_SERVER['HTTP_HOST'].dirname($_SERVER['PHP_SELF']).'errorpage.php');
		}
		echo( '<a href="index.php">' . _sb('home') . '</a>' );
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
