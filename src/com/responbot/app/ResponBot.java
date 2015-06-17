package com.responbot.app;

import twitter4j.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class ResponBot {
	public static void main(String[] args) throws TwitterException {
		String UserNameToFollowFP = "@jaec";
		String msgFP = "hoal";

		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream stream = loader
					.getResourceAsStream("responbot.properties");
			InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
			Properties properties = new Properties();
			properties.load(isr);

			Enumeration<Object> enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				// System.out.println(key + ": " + value);
				if (StringUtils.equalsIgnoreCase(key, "usuario")) {
					UserNameToFollowFP = value.trim();
				}

				if (StringUtils.equalsIgnoreCase(key, "mensaje")) {
					msgFP = value.trim();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		/*
		 * if (args.length < 1) { System.out .println(
		 * "Usage: java twitter4j.examples.PrintFilterStream [follow(comma separated numerical user ids)] [track(comma separated filter terms)]"
		 * ); System.exit(-1); }
		 */

		final String UserNameToFollow = StringUtils.remove(UserNameToFollowFP,
				'@');
		final Twitter twitter = new TwitterFactory().getInstance();
		User userToFollow = twitter.showUser(UserNameToFollow);
		final Long userIdToFollow = userToFollow.getId();
		final String msg = msgFP;

		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status status) {
				if (status.getUser().getId() == userIdToFollow) {
					System.out.println("@" + status.getUser().getScreenName()
							+ " - " + status.getText());
					/*
					 * String url = "https://twitter.com/" + UserNameToFollow +
					 * "/status/" + status.getId();
					 */
					String tweet = StringUtils.abbreviate("@"
							+ UserNameToFollow + " " + msg, 124);
					StatusUpdate statusUpdate = new StatusUpdate(tweet + " "
							+ System.currentTimeMillis());

					statusUpdate.inReplyToStatusId(status.getId());

					try {
						twitter.updateStatus(statusUpdate);
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onDeletionNotice(
					StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:"
						+ statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:"
						+ numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId
						+ " upToStatusId:" + upToStatusId);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};

		final TwitterStream twitterStream = new TwitterStreamFactory()
				.getInstance();
		twitterStream.addListener(listener);
		ArrayList<Long> follow = new ArrayList<Long>();
		ArrayList<String> track = new ArrayList<String>();
		/*
		 * for (String arg : args) { if (isNumericalArgument(arg)) { for (String
		 * id : arg.split(",")) { follow.add(Long.parseLong(id)); } } else {
		 * track.addAll(Arrays.asList(arg.split(","))); } }
		 */

		follow.add(userIdToFollow);

		long[] followArray = new long[follow.size()];
		for (int i = 0; i < follow.size(); i++) {
			followArray[i] = follow.get(i);
		}
		String[] trackArray = track.toArray(new String[track.size()]);

		// filter() method internally creates a thread which manipulates
		// TwitterStream and calls these adequate listener methods continuously.
		twitterStream.filter(new FilterQuery(0, followArray, trackArray));

		// final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Closing...");
				twitterStream.cleanUp();
				twitterStream.shutdown();
			}
		});
	}

	@SuppressWarnings("unused")
	private static boolean isNumericalArgument(String argument) {
		String args[] = argument.split(",");
		boolean isNumericalArgument = true;
		for (String arg : args) {
			try {
				Integer.parseInt(arg);
			} catch (NumberFormatException nfe) {
				isNumericalArgument = false;
				break;
			}
		}
		return isNumericalArgument;
	}
}
