package com.responbot.app;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.List;

public class GetUserTL {
	public static void main(String[] args) {
		// gets Twitter instance with default credentials
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			List<Status> statuses;
			String user;
			if (args.length == 1) {
				user = args[0];
				statuses = twitter.getUserTimeline(user);
			} else {
				/*user = twitter.verifyCredentials().getScreenName();
				statuses = twitter.getUserTimeline();*/
				user = "Milenio";
				statuses = twitter.getUserTimeline(user);
			}
			System.out.println("Showing @" + user + "'s user timeline.");
			for (Status status : statuses) {
				System.out.println("@" + status.getUser().getScreenName()
						+ " - " + status.getText());
			}
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get timeline: " + te.getMessage());
			System.exit(-1);
		}
	}
}
