<?php
	/**
	* Hit counter widget.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class CounterTotals extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function CounterTotals () {
			$this->plugin = 'CounterTotals';
			$this->loadPrefs();
		}
		
		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return _sb('counter_title');
		}
		
		function getContent () {
			$str = stat_all();
			return $str;
		}
	}
?>
