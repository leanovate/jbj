<?php
	/**
	* Most recent entries widget.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class RecentEntries extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function RecentEntries () {
			$this->plugin = 'RecentEntries';
			$this->loadPrefs();
		}
		
		/* ------ PREFERENCES ------ */
		
		function defaultPrefs () {
			$arr = parent::defaultPrefs();
			$arr['count'] = 10;
			
			return $arr;
		}
		
		/* ------ OPTIONS ------ */
		
		function optionsForm () {
			$label_count = 'Number of entries to display (1-100)';
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
			return _sb('menu_most_recent_entries');
		}
		
		function getContent () {
			$str = '';
			
			$entry_file_array = blog_entry_listing();

			// Grab the next X number of entries
			$file_array = array();
			for ( $i = 0; $i < min(count($entry_file_array),$this->prefs['count']); $i++ ) {
				array_push( $file_array, $entry_file_array[ $i ] );
			}

			// Read entry files
			$contents = array();
			for ( $i = 0; $i < count( $file_array ); $i++ ) {
				list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $file_array[ $i ] );
				array_push( $contents, array( 	'path' => ( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename ),
												'entry' => $entry_filename,
												'year' => $year_dir,
												'month' => $month_dir ) );
			}
			
			if ( $contents ) {
				$base_permalink_url = baseurl();

				for ( $i = 0; $i <= count( $contents ) - 1; $i++ ) {
					$blog_entry_data = blog_entry_to_array( $contents[$i][ 'path' ] );

					$entry_array = array();
					$entry_array[ 'subject' ] = blog_to_html( $blog_entry_data[ 'SUBJECT' ], false, false );

					$entry = fileio::strip_extension( $contents[$i][ 'entry' ] );

					$entry_array[ 'permalink' ][ 'url' ] = $base_permalink_url . 'index.php?entry=' . $entry;

					$str  .= '<a href="' . $entry_array[ 'permalink' ][ 'url' ] . '">' . $entry_array[ 'subject' ] . '</a><br />';
				}
			}
			
			return $str;
		}
		
	}
?>
