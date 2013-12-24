<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	$page_title = _sb("contact_title");

	global $logged_in;
	$logged_in = logged_in( false, true );
	
	if ( !session_id() ) {
		session_start();
	}
	$_SESSION[ 'capcha_contact' ] = sb_get_capcha();
	
	// ---------------
	// POST PROCESSING
	// ---------------
	
	// The user must have cookies enabled in order to send contacts - this helps with blank emails
	if (!isset($_SESSION['cookies_enabled'])) {
		redirect_to_url('errorpage.php');
		// header('location: http://'.$_SERVER['HTTP_HOST'].dirname($_SERVER['PHP_SELF']).'errorpage-nocookies.php');
	}
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $blog_config, $theme_vars; 
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('contact_title');
		
		// PAGE CONTENT BEGIN
		ob_start(); ?>
		
		<?php echo ( _sb('contact_instructions') ); ?><p />
		<form action="contact_cgi.php" method="post">
		<p>
		<label for="name"><?php echo( _sb('name') ); ?></label><br />
		<input type="text" name="name" id="name" size="40" /><br /><br />
		<label for="email"><?php echo( _sb('email') ); ?></label><br />
		<input type="text" name="email" id="email" size="40" /><br /><br />
		<label for="subject"><?php echo( _sb('subject') ); ?></label><br />
		<input type="text" name="subject" id="subject" size="40" /><br /><br />
		<label for="text"><?php echo( _sb('comment') ); ?></label><br />
		<textarea style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;" id="text" name="comment" rows="20" cols="50"></textarea><br /><br />
		
		<?php
		if ( $blog_config->getTag('BLOG_ENABLE_CAPCHA') == 0 ) {
			echo('<!-- Anti-spam disabled -->');
			echo('<input type="hidden" name="capcha_contact" id="capcha_contact" value="' . $_SESSION[ 'capcha_contact' ] . '" maxlength="6" /><br /><br />'); 
		} else {
			echo('<label for="capcha_contact">');
			if ( function_exists('imagecreate') && $blog_config->getTag('BLOG_ENABLE_CAPCHA_IMAGE') ) {
				echo ( _sb('contact_capcha') . '<br /><img src="capcha.php?entry=contact" alt="CAPCHA" />' );
			} else {
				echo ( _sb('contact_capcha') . '<b>' . sb_str_to_ascii( $_SESSION[ 'capcha_contact' ] ) . '</b>' );
			}
			echo('</label><br />');
			echo('<input type="text" name="capcha_contact" id="capcha_contact" value="" maxlength="6" /><br /><br />');
		} 
		?>
		</p>
		<hr />
		<p>
		<input type="submit" name="submit" value="<?php echo( _sb('submit_btn') ); ?>" />
		</p>
		</form>
		<?php
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}
	
	// ----
	// HTML
	// ----
	
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
	
?>
