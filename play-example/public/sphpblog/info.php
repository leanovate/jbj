<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title = _sb('info_title');
	
	// ---------------
	// POST PROCESSING
	// ---------------
	if ( array_key_exists( 'info_keywords', $_POST ) && array_key_exists( 'info_description', $_POST ) && array_key_exists( 'info_copyright', $_POST ) ) {
		
		$new_config = new Configuration();
		$new_config->read_file();
		
		// @htmlspecialchars( $str, ENT_QUOTES, $GLOBALS['lang_string']['php_charset'] );
		$new_config->setTag('INFO_KEYWORDS', 		sb_stripslashes($_POST['info_keywords']));
		$new_config->setTag('INFO_DESCRIPTION', 	sb_stripslashes($_POST['info_description']));
		$new_config->setTag('INFO_COPYRIGHT', 		sb_stripslashes($_POST['info_copyright']));
		$new_config->setTag('TRACKING_CODE', 		$_POST['tracking_code']);
		
		global $ok;
		$ok = $new_config->write_file();
								
		if ( $ok === true ) { 
			redirect_to_url( 'index.php' );
		}
	}
	
	// -----------
	// PAGE CONTENT
	// -----------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $blog_config, $theme_vars, $blog_theme;
	
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('info_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		if ( array_key_exists( "info_keywords", $_POST ) && array_key_exists( "info_description", $_POST ) && array_key_exists( "info_copyright", $_POST ) ) {	
			// Check to see if we're posting data...
			global $ok;
			if ( $ok !== true ) {
				echo( _sb('info_error') . $ok . '<p />' );
			} else {
				echo( _sb('info_success') . '<p />' );
			}
			echo( '<a href="index.php">' . _sb('home') . '</a>' );
		} else {
			?>
			
			<?php echo( _sb('info_instructions') ); ?><p />
			
			<form action="info.php" method="post" name="info" name="info">
				
				<label for="info_keywords"><?php echo( _sb('info_keywords') ); ?></label><br />
				<textarea style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;" id="text" name="info_keywords" rows="5" cols="50" autocomplete="OFF"><?php echo($blog_config->getTag('INFO_KEYWORDS')); ?></textarea><br /><br />
				
				<label for="info_description"><?php echo( _sb('info_description') ); ?></label><br />
				<textarea style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;" id="text" name="info_description" rows="5" cols="50" autocomplete="OFF"><?php echo($blog_config->getTag('INFO_DESCRIPTION')); ?></textarea><br /><br />
				
				<label for="info_copyright"><?php echo( _sb('info_copyright') ); ?></label><br />
				<textarea style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;" id="text" name="info_copyright" rows="5" cols="50" autocomplete="OFF"><?php echo($blog_config->getTag('INFO_COPYRIGHT')); ?></textarea><br /> <br />

				<label for="tracking_code"><?php echo( _sb('tracking_code') ); ?></label><br />
				<textarea style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;" id="text" name="tracking_code" rows="5" cols="50" autocomplete="OFF"><?php echo($blog_config->getTag('TRACKING_CODE')); ?></textarea><br />

				<hr />

				<input type="submit" name="submit" value="<?php echo( _sb('submit_btn') ); ?>" />
			</form>
			
			<?php 
		}
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
