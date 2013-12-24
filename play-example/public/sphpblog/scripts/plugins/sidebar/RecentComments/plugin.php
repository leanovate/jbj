<?php
	/**
	* Most recent comments widget.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class RecentComments extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function RecentComments () {
			$this->plugin = 'RecentComments';
			$this->loadPrefs();
		}
		
		/* ------ PREFERENCES ------ */
		
		function defaultPrefs () {
			$arr = parent::defaultPrefs();
			$arr['count'] = 5;
			$arr['dateFormat'] = '%x';
			
			return $arr;
		}
		
		/* ------ OPTIONS ------ */
		
		function optionsForm () {
			$label_count = 'Number of entries to display (1-100)';
			$value_count = $this->prefs['count'];
			
			$label_dateFormat = 'Date format as defined by the PHP <a href="http://us2.php.net/manual/en/function.strftime.php" target="_blank">strftime()</a> function';
			$value_dateFormat = $this->prefs['dateFormat'];
			
			$save = _sb('submit_btn');
			
			$width = $GLOBALS['theme_vars']['max_image_width'] - 20;
		
			ob_start(); ?>
			<!-- FORM -->
			<form method="post" onsubmit="return validate(this)">
				<label for="count"><?php echo( $label_count ); ?></label><br />
				<input type="text" name="count" value="<?php echo( $value_count ); ?>" autocomplete="OFF" size="40" style="width: <?php echo( $width ); ?>px;"><p />
				
				<label for="dateFormat"><?php echo( $label_dateFormat ); ?></label><br />
				<input type="text" name="dateFormat" value="<?php echo( $value_dateFormat ); ?>" autocomplete="OFF" size="40" style="width: <?php echo( $width ); ?>px;"><p />
				
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
				
				if ( array_key_exists( 'dateFormat', $_POST ) ) {
					$dateFormat = sb_stripslashes( $_POST['dateFormat'] );
					if ( !empty( $dateFormat ) ) {
						$this->prefs['dateFormat'] = $dateFormat;
						$this->savePrefs();
					}
				}
				
			}
		}
		
		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return _sb('menu_most_recent');
		}
		
		function getContent () {
			$str = $this->get_most_recent();
			
			return $str;
		}

		function get_most_recent () {
			$comment_array = blog_comment_listing();
			
			$str = '';
			$count = 0;
			
			for ( $i = 0; $i < count($comment_array); $i++ ) {
				list( $entry_filename, $year_dir, $month_dir, $comment_filename, $moderation_flag ) = explode( '|', $comment_array[ $i ] );
				
				if ( $moderation_flag != 'H') {
				
					$comment_data = comment_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . fileio::strip_extension( $entry_filename ) . '/comments/' . $comment_filename );
					
					if ( $comment_data !== false) {
						
						// Comment Author
						$name = $comment_data[ 'NAME' ];
						if ( strlen( $name ) > 40 ) {
							$name = substr( $name, 0, 40 );
							$name = substr( $name, 0, strrpos( $name, ' ' ) ) . '...';
						}
						
						// Comment Content
						$content = $comment_data[ 'CONTENT' ];
						$content = blog_to_html( $content, false, true );
						$content = str_replace( '<br />', ' ', $content );
						if ( strlen( $content ) > 60 ) {
							$content = substr( $content, 0, 60 );
							$content = substr( $content, 0, strrpos( $content, ' ' ) ) . '...';
						}
						
						// Comment Date
						$date = $comment_data[ 'DATE' ];
						
						// HTML
						
						if ( $GLOBALS['blog_config']->getTag('BLOG_COMMENTS_POPUP') == 1 ) {
							$width = $GLOBALS[ 'theme_vars' ][ 'popup_window' ][ 'width' ];
							$height = $GLOBALS[ 'theme_vars' ][ 'popup_window' ][ 'height' ];
							$str  .= '<a href="javascript:openpopup(\'comments.php?y='.$year_dir.'&amp;m='.$month_dir.'&amp;entry='.fileio::strip_extension( $entry_filename ).'\','.$width.','.$height.',true)">'.$name.'</a><br />';
						} else {
							$str  .= '<a href="comments.php?y='.$year_dir.'&amp;m='.$month_dir.'&amp;entry='.fileio::strip_extension( $entry_filename ).'">'.$name.'</a><br />';
						}
						
						$str .= strftime( $this->prefs['dateFormat'], $date ) . '<br />';
						$str .= $content . '<p />';
						
						$count++;
						if ( $count >= $this->prefs['count'] ) {
							break;
						}
					}
						
				}
			}
			
			return $str;
		}
		
	}
?>
