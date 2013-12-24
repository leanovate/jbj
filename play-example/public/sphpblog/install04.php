<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, false );

	$page_title = _sb('install04_title');
	require_once('scripts/sb_header.php');
	
	// $ok = create_password( sb_stripslashes( $_POST['user'] ), sb_stripslashes( $_POST['pass'] ) );
	// $logged_in = $ok;
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $ok, $blog_config;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('install04_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		echo( _sb('install04_instructions') . '<p />' );
		
		$hashedUser = crypt( sb_stripslashes( $_POST['user'] ) );
		$hashedPass = crypt( sb_stripslashes( $_POST['pass'] ) );
		?>
		
		<label for="phpfile"><?php echo( _sb('code') ); ?></label><br />
<textarea style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;" name="phpfile" rows="6" cols="40">&lt;?php
	// Save file as 'password.php' and FTP it into the 'config' directory.
	$username = '<?php echo( $hashedUser ); ?>';
	$password = '<?php echo( $hashedPass ); ?>';
?&gt;</textarea>
		
		<?php 
		echo( '<p />' );
		echo( '<a href="install05.php?blog_language=' . $blog_config->getTag('BLOG_LANGUAGE') . '">' . _sb('continue') . '</a><p />' );
		echo( _sb('information') );
		echo( '<p />' );
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
