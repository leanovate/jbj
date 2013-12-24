<?php
	/**
	* Random entry widget.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class RandomEntry extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function RandomEntry () {
			$this->plugin = 'RandomEntry';
			$this->loadPrefs();
		}
		
		/* ------ PREFERENCES ------ */
		
		function defaultPrefs () {
			$arr = parent::defaultPrefs();
			$arr['count'] = 5;
			
			return $arr;
		}
		
		/* ------ OPTIONS ------ */
		
		function optionsForm () {
			$label_count = 'Number of entries to display (1-100)';// $GLOBALS['lang_string']['blog_avatar'];
			$value_count = $this->prefs['count'];
			$save = _sb('submit_btn');
			$width = $GLOBALS['theme_vars']['max_image_width'] - 20;
		
			ob_start(); ?>
			<!-- FORM -->
			<form method="post" onsubmit="return validate(this)">
				<label for="count"><?php echo( $label_count ); ?></label><br />
				<input type="text" name="count" value="<?php echo( $value_count ); ?>" autocomplete="OFF" size="40" style="width: <?php echo( $width ); ?>px;"><p />
				
				<input type="submit" name="save" value="<?php echo( $save ); ?>" />
			</form>
			<?php
			$str = ob_get_clean();
		
			return $str;
		}
		
		function optionsPost () {	
			if ( array_key_exists( 'save', $_POST ) ) {			
				if ( array_key_exists( 'count', $_POST ) ) {
					$count = intval( sb_stripslashes( $_POST['count'] ) );
					if ( is_numeric( $count ) && $count >= 1 && $count <= 100 ) {
						$this->prefs['count'] = $count;
						$this->savePrefs();
					}
				}
			}
		}
		
		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return _sb('menu_random_entry');
		}
		
		function getContent () {
			$str = '';
			
			$entry_array = blog_entry_listing();
			shuffle( $entry_array );
			
			for ( $i = 0; $i < min(count($entry_array),$this->prefs['count']); $i++ ) {
				// Randomly select an entry to display
				$randomIndex = $i;
				list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $entry_array[ $randomIndex ] );
				
				// Read the entry and grab the subject line
				$blog_entry_data = blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
				
				// Format the subject line to make sure there's no extra HTML that will mess up formatting...
				// blog_to_html( $str, $comment_mode, $strip_all_tags, $add_no_follow=false, $emoticon_replace=false )
				$subject = blog_to_html( $blog_entry_data[ 'SUBJECT' ], false, true, false, false );
				
				// We're just going to make this a relative link... Uncomment below to make a full URL link:
				$base_permalink_url = '';
				// $base_permalink_url = baseurl();
				
				// Strip the file extension
				$entry_no_ext = fileio::strip_extension( $entry_filename );
				$link = $base_permalink_url . 'index.php?entry=' . $entry_no_ext;
				
				// Create array for proper 'widget' format
				$str .= sprintf( '<a href="%s">%s</a><br />', $link, $subject );
			}
			
			return $str;
		}
	}
?>
