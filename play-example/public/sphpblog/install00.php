<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, false );

	$page_title = _sb('install00_title');
	require_once('scripts/sb_header.php');
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $blog_config;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('install00_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		echo( _sb('install00_instructions') . '<p />' );
		?>
			<form action="install01.php" method="post">
				<?php
					$translation_arr = get_installed_translations();
					
					$dropdown_arr = array();
					for ($i=0; $i < count($translation_arr); $i++) {				
						$lang_dir = $translation_arr[$i]['directory'];
						$lang_name = $translation_arr[$i]['name'];
						
						$item = array();
						$item['label'] = $lang_name;
						$item['value'] = $lang_dir;
						if ( $blog_config->getTag('BLOG_LANGUAGE') == $item['value'] ) {
							$item['selected'] = true;
						}
						array_push( $dropdown_arr, $item );
						
					}

                                        // sort array
                                        // Obtain a list of columns
                                        $label = array();
                                        for ($i = 0; $i < sizeof($dropdown_arr); $i++) {
                                                $label[] = $dropdown_arr[$i]['label'];
                                        }
                                        array_multisort($label, $dropdown_arr);

					echo( HTML_dropdown( _sb('blog_choose_language'), "blog_language", $dropdown_arr ) );
				?>
				<p />
				
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
