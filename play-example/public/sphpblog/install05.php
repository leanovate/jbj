<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( false, false );

	$page_title = _sb('install05_title');
	require_once('scripts/sb_header.php');
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $blog_config;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('install05_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		echo( _sb('install05_instructions') . '<p />' );
		?>
		
		<hr />
		
		<form action="install06.php?blog_language=<?php echo( $blog_config->getTag('BLOG_LANGUAGE') ); ?>" method="post">
			<label for="user"><?php echo( _sb('username') ); ?></label><br />
			<input type="text" name="user" autocomplete="OFF" size="40"><p />
			
			<label for="pass"><?php echo( _sb('password') ); ?></label><br />
			<input type="password" name="pass" autocomplete="OFF" size="40"><p />
			
			<input type="submit" name="submit" value="<?php echo( _sb('submit_btn') ); ?>" />
		</form>
		
		<?php 
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
