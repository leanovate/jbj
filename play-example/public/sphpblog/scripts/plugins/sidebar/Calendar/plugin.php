<?php
	/**
	* Calendar widget.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class Calendar extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function Calendar () {
			$this->plugin = 'Calendar';
			$this->loadPrefs();
		}
		
		/* ------ PREFERENCES ------ */
		
		function defaultPrefs () {
			$arr = parent::defaultPrefs();
			$arr['firstDay'] = 'sunday';
			$arr['dateFormat'] = '%x';
			
			return $arr;
		}
		
		/* ------ OPTIONS ------ */
		
		function optionsForm () {
			$label_firstDay = _sb('label_calendar_start');
			$sunday = _sb('cal_sunday');
			$monday = _sb('cal_monday');
			$value_firstDay = $this->prefs['firstDay'];
			
			$label_dateFormat = 'Date format as defined by the PHP <a href="http://us2.php.net/manual/en/function.strftime.php" target="_blank">strftime()</a> function';
			$value_dateFormat = $this->prefs['dateFormat'];
			
			$save = _sb('submit_btn');
			
			ob_start(); ?>
			<!-- FORM -->
			<form method="post" onsubmit="return validate(this)">
				<label for="firstDay"><?php echo( $label_firstDay ); ?></label>
				<select name="firstDay">
					<option label="<?php echo( $sunday ); ?>" value="sunday"<?php if ( $value_firstDay == 'sunday') { echo ' selected'; } ?>><?php echo( $sunday ); ?></option>
					<option label="<?php echo( $monday ); ?>" value="monday"<?php if ( $value_firstDay == 'monday') { echo ' selected'; } ?>><?php echo( $monday ); ?></option>
				</select><p />
				
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
			
				if ( array_key_exists( 'firstDay', $_POST ) ) {
					$firstDay = sb_stripslashes( $_POST['firstDay'] );
					if ( $firstDay == 'sunday' || $firstDay == 'monday' ) {
						$this->prefs['firstDay'] = $firstDay;
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
			return _sb('menu_calendar');
		}
		
		function getContent () {
			return $this->getCalendar( $GLOBALS['month'], $GLOBALS['year'], $GLOBALS['day'] );
		}
		
		/* ------ CUSTOM ------ */
		
		function getCalendar ( $m, $y, $d ) {

			if ( !isset( $m ) ) {
				$m = date( 'm' );
			}
			if ( !isset( $y ) ) {
				$y = date( 'y' );
			}
			if ( !isset( $d ) ) {
				$d = date( 'd' );
			}
			
			if( $this->prefs['firstDay'] == 'sunday' ) {
				$date_string = mktime(0, 0, 0, $m, 2, $y ); // Use this for starting the calendar on Sunday
			} else {
				$date_string = mktime(0, 0, 0, $m, 1, $y ); //The date string we need for some info... saves space ^_^
			}
			
			$day_start = date( 'w', $date_string ); //The number of the 1st day of the week
			if ( strftime( '%w', mktime( 0, 0, 0, 1, 1, 2007 ) ) != 0 ) {
				$day_start = ( $day_start + 6 ) % 7;
			}
			
			//Calculate the previous/next month/year
			if ( $m < 12 ) {
				$next_month = $m + 1;
				$next_year = $y + 2000;
			} else {
				$next_year = $y + 1;
				$next_month = 1;
			}
			if ( $m > 1 ) {
				$previous_month = $m - 1;
				$previous_year = $y;
			} else {
				$previous_year = $y - 1;
				$previous_month = 12;
			}
			
			//$entries = fileio::file_listing( CONTENT_DIR . $y . '/' . $m . '/', array( '.txt', '.gz' ) );
			$entries = blog_entry_listing();
			sort ( $entries );
			
			// Remove not current month/day entries
			$temp_entries=array();
			for ( $i = 0; $i < count( $entries ); $i++ ) {
				if ( ( substr( $entries[ $i ], 5, 2 ) == $y ) && ( substr( $entries[ $i ], 7, 2 ) == $m ) ) {
					array_push( $temp_entries, $entries[ $i ] );
				}
			}
	
			// Don't let go before the first article
			if ( substr( $entries[ 0 ], 7, 2 ) + ( substr( $entries[ 0 ], 5, 2 ) * 12 ) >=
				$y*12+$m ) {
				$previous_year = $y+2000;
				$previous_month = $m;
			}
			// Don't let go past now
			if ( date( 'm' ) + ( date( 'y' ) * 12 ) <=
				$y*12+$m ) {
				$next_year = $y+2000;
				$next_month = $m;
			}
	
			$entries=$temp_entries;
			unset( $temp_entries );
	
			// Loop Through Days
			$counts = Array();
			for ( $i = 0; $i < count( $entries ); $i++ ) {
				$temp_index = substr( $entries[$i], 9, 2 )-1;
				$temp_entry = substr( $entries[$i], 0, 11 );
	
				// Count the number of entries on this day
				$counts[$temp_index] = 1;
				for ( $j = $i + 1; $j < count( $entries ); $j++ ) {
					if ( $temp_entry == substr( $entries[$j], 0, 11 ) ) {
						$counts[$temp_index]++;
					} else {
						break;
					}
				}
				$i = $j - 1;
			}
	
			$str = '
			<table style="border-style: none; text-align: center; cellpadding: 0px; cellspacing: 0px;" class="calendar">
			<tr>
			<td style="text-align: center">';
			if ( ( ( $previous_year%100 )!=$y ) || ( $previous_month!=$m ) ) {
				$str .= '<a href="' . BASEURL . 'index.php?y=' . sprintf( '%02d', $previous_year % 100 ) . '&amp;m=' . sprintf( '%02d', $previous_month ) .'">&laquo;</a>';
			}
			$str .= '</td>
			<td style="text-align: center" colspan="5"><b>' . ucwords( strftime( '%B %Y', $date_string) ) . '</b></td>
			<td style="text-align: center">';
			if ( ( ( $next_year%100 )!=$y ) || ( $next_month!=$m ) ) {
				$str .= '<a href="' . BASEURL . 'index.php?y=' . sprintf( '%02d', $next_year % 100 ) . '&amp;m=' . sprintf( '%02d', $next_month ) .'">&raquo;</a>';
			}
			$str .= '</td>
			</tr>
			<tr>';
			
			if( $this->prefs['firstDay'] == 'sunday' ) {		
				// This is for the Sunday starting date
				for ( $i=0; $i<7; $i++ ) {
					if ( $day_start!=0 ) {
						$str .= '<td>' . ucwords( strftime( '%a', mktime(0, 0, 0, 1, ($i+0)%7, 1990 ) ) ) . '</td>';
					} else {
						$str .= '<td>' . ucwords( strftime( '%a', mktime(0, 0, 0, 1, ($i+7)%7, 1990 ) ) ) . '</td>';
					}
				}
			} else {		
				for ( $i=0; $i<7; $i++ ) {
					if ( $day_start!=0 ) {
						$str .= '<td>' . ucwords( strftime( '%a', mktime(0, 0, 0, 1, ($i+1)%7, 1990 ) ) ) . '</td>';
					} else {
						$str .= '<td>' . ucwords( strftime( '%a', mktime(0, 0, 0, 1, ($i+8)%7, 1990 ) ) ) . '</td>';
					}
				}
			}
			
			$str .= '</tr><tr>';
			
			//The empty columns before the 1st day of the week
			for ( $i = 0; $i<$day_start; $i++ ) {
				$str .= '<td>&nbsp;</td>';
			}
			$current_position = $day_start; //The current (column) position of the current day from the loop
			$total_days_in_month = date( 't', $date_string); //The total days in the month for the end of the loop
	
			//Loop all the days from the month
			for ( $i = 1; $i<=$total_days_in_month; $i++) {
				if ( mktime( 0, 0, 0, $m, $i, $y ) == mktime( 0, 0, 0 ) ) {
					$str .= '<td style="text-align: center; text-decoration: underline">';
				} else {
					$str .= '<td style="text-align: center">';
				}
				
				if ( isset($counts[$i-1]) && $counts[$i-1] > 0 ) {
					$str .= '<a href="' . BASEURL . 'index.php?d=' . sprintf( '%02d', $i) . '&amp;m=' . sprintf( '%02d', $m ) . '&amp;y=' . sprintf( '%02d', $y % 100 ) . '" title="' . $counts[$i-1] . '">' . $i . '</a>';
				} else {
					$str  .= $i;
				}
				
				$str .= '</td>';
				
				$current_position++;
				
				if ( $current_position == 7 ) {
					$str .= '</tr><tr>';
					$current_position = 0;
				}
			}
			$end_day = 7-$current_position; //There are
	
			//Fill the last columns
			for ( $i = 0; $i<$end_day; $i++ ) {
				$str .= '<td></td>';
			}
			$str .= '</tr><tr>';
			
			// Fixed per Sverd1 March 17, 2006
			$str .= '<td colspan="7" style="text-align: center">' . strftime( '<a href="' . BASEURL . 'index.php?y=%y&amp;m=%m&amp;d=%d">' . $this->prefs['dateFormat'] ) . '</a></td></tr></table>'; // Close the table
			return $str;
		}
	}
?>
