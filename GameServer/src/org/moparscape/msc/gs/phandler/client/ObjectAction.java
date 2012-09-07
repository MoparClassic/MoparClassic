package org.moparscape.msc.gs.phandler.client;

import org.moparscape.msc.gs.event.handler.objectaction.ObjectActionManager;
import org.moparscape.msc.gs.event.handler.objectaction.ObjectActionParam;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.event.WalkToObjectEvent;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.landscape.ActiveTile;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.Action;

public class ObjectAction implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	private static final ObjectActionManager oam = new ObjectActionManager();

	public void handlePacket(Packet p, IoSession session) {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if (player.isBusy()) {
			if (player.getStatus() != Action.AGILITYING)
				player.resetPath();

			return;
		}

		player.resetAll();
		ActiveTile t = world.getTile(p.readShort(), p.readShort());
		final GameObject object = t.getGameObject();
		final int click = pID == 51 ? 0 : 1;
		player.setClick(click);
		if (object == null) {
			t.cleanItself();
			player.setSuspiciousPlayer(true);
			return;
		}
		world.addEntryToSnapshots(new Activity(player.getUsername(), player
				.getUsername()
				+ " clicked on a object ("
				+ object.getID()
				+ ") at: "
				+ player.getX()
				+ "/"
				+ player.getY()
				+ "|"
				+ object.getX() + "/" + object.getY()));

		player.setStatus(Action.USING_OBJECT);
		Instance.getDelayedEventHandler().add(
				new WalkToObjectEvent(player, object, false) {
					public void arrived() {
						if (owner.isBusy() || owner.isRanging()
								|| !owner.nextTo(object)
								|| owner.getStatus() != Action.USING_OBJECT) {
							return;
						}
						world.addEntryToSnapshots(new Activity(owner
								.getUsername(), owner.getUsername()
								+ " used an Object (" + object.getID()
								+ ") @ " + object.getX() + ", "
								+ object.getY()));
						owner.resetAll();
						oam.trigger(object.getID(), new ObjectActionParam(
								owner, object, click));
						return;
						/*try {

							} else if (EntityHandler.getAgilityDef(object
									.getID()) != null) {
								handleAgility(object);
							} else {
								switch (object.getID()) {
								case 613: // Shilo cart
									if (object.getX() != 384
											|| object.getY() != 851) {
										return;
									}
									owner.setBusy(true);
									owner.getActionSender()
											.sendMessage(
													"You search for a way over the cart");
									Instance.getDelayedEventHandler().add(
											new ShortEvent(owner) {
												public void action() {
													owner.getActionSender()
															.sendMessage(
																	"You climb across");
													if (owner.getX() <= 383) {
														owner.teleport(386,
																851, false);
													} else {
														owner.teleport(383,
																851, false);
													}
													owner.setBusy(false);
												}
											});
									break;
								case 643: // Gnome tree stone
									if (object.getX() != 416
											|| object.getY() != 161) {
										return;
									}// getCurStat(14
									owner.setBusy(true);
									owner.getActionSender()
											.sendMessage(
													"You twist the stone tile to one side");
									Instance.getDelayedEventHandler().add(
											new ShortEvent(owner) {
												public void action() {
													owner.getActionSender()
															.sendMessage(
																	"It reveals a ladder, you climb down");
													owner.teleport(703, 3284,
															false);
													owner.setBusy(false);
												}
											});
									break;
								case 638: // First roots in gnome cave
									if (object.getX() != 701
											|| object.getY() != 3280) {
										return;
									}
									// door
									owner.setBusy(true);
									owner.getActionSender().sendMessage(
											"You push the roots");
									Instance.getDelayedEventHandler().add(
											new ShortEvent(owner) {
												public void action() {
													owner.getActionSender()
															.sendMessage(
																	"They wrap around you and drag you forwards");
													owner.teleport(701, 3278,
															false);
													owner.setBusy(false);
												}
											});
								case 639: // Second roots in gnome cave
									if (object.getX() != 701
											|| object.getY() != 3279) {
										return;
									}
									owner.setBusy(true);
									owner.getActionSender().sendMessage(
											"You push the roots");
									Instance.getDelayedEventHandler().add(
											new ShortEvent(owner) {
												public void action() {
													owner.getActionSender()
															.sendMessage(
																	"They wrap around you and drag you forwards");
													owner.teleport(701, 3281,
															false);
													owner.setBusy(false);
												}
											});
									break;
								default:
									owner.getActionSender().sendMessage(
											"Nothing interesting happens.");
									return;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}*/
					}

					/*private void handleAgility(final GameObject object) {
						if (!World.isMembers()) {
							owner.getActionSender()
									.sendMessage(
											"This feature is only avaliable on a members server");
							return;
						}
						final AgilityDef def = EntityHandler
								.getAgilityDef(object.getID());
						if (owner.getCurStat(16) < def.getReqLevel()) {
							owner.getActionSender().sendMessage(
									"You need an agility level of "
											+ def.getReqLevel()
											+ " to try this obstacle");
							return;
						}

						owner.setBusy(true);
						owner.setStatus(Action.AGILITYING);
						owner.getActionSender().sendMessage(def.getMessage());

						if (Formulae.getHeight(def.getY()) == Formulae
								.getHeight(def.getToY()))
							owner.setPath(new Path(def.getX(), def.getY(), def
									.getToX(), def.getToY(), true));
						else
							owner.teleport(def.getToX(), def.getToY(), false);

						Instance.getDelayedEventHandler().add(
								new DelayedEvent(owner, 100) {
									private boolean testedFail = false;

									public void run() {
										if (def.canFail() && !testedFail) {
											if (owner.getX() >= ((def.getToX() + def
													.getX()) / 2)
													&& owner.getY() >= ((def
															.getToY() + def
															.getY()) / 2)) // half
											// way
											// accross
											// the
											// obstacle
											{
												int damage = Formulae
														.failObstacle(
																owner,
																def.getReqLevel());
												if (damage != -1) {
													owner.getActionSender()
															.sendMessage(
																	"You slip off the obstacle!");
													owner.teleport(
															def.getFailX(),
															def.getFailY(),
															false);
													owner.setBusy(false);
													owner.setStatus(Action.IDLE);
													owner.setLastDamage(damage);
													int newHp = owner.getHits()
															- damage;
													owner.setHits(newHp);
													java.util.ArrayList<Player> playersToInform = new java.util.ArrayList<Player>();
													playersToInform
															.addAll(owner
																	.getViewArea()
																	.getPlayersInView());
													for (Player p : playersToInform)
														p.informOfModifiedHits(owner);
													stop();
												}

												testedFail = true;
											}
										}

										if (owner.getX() == def.getToX()
												&& owner.getY() == def.getToY()) {
											owner.getActionSender()
													.sendMessage(
															"You successfully make it to the other side of the obstacle");
											owner.setBusy(false);
											owner.setStatus(Action.IDLE);
											owner.incExp(16, def.getExp(), true);
											owner.getActionSender()
													.sendStat(16);

											AgilityCourseDef course = EntityHandler
													.getAgilityCourseDef(object
															.getID());

											if (owner.getAgilityCourseDef() != null) // We're
											// currently
											// doing
											// a
											// course
											{
												course = owner
														.getAgilityCourseDef();
												if (def.getOrder() == (owner
														.getCurrentCourseObject() + 1)) // We've
												// used
												// the
												// next
												// object
												// in
												// the
												// sequence
												{
													if (object.getID() == course
															.getEndID()
															&& object.getX() == course
																	.getEndX()
															&& object.getY() == course
																	.getEndY()) // We've
													// finished
													// the
													// course
													{
														owner.getActionSender()
																.sendMessage(
																		"You have completed the "
																				+ course.getName()
																				+ " obstacle course!");
														owner.incExp(
																16,
																course.getExp(),
																true);
														owner.setAgilityCourseDef(null);
														owner.setCurrentCourseObject(-1);
													} else
														owner.setCurrentCourseObject(def
																.getOrder()); // Continue
													// the
													// sequence
												} else {
													owner.setAgilityCourseDef(null); // We've
													// broken
													// the
													// sequence,
													// end
													// the
													// course
													owner.setCurrentCourseObject(-1);
												}
											} else // We're not doing a course,
											// so check if this object
											// is the start of a new
											// course
											{
												if (course != null) // It is, so
												// we start
												// a new
												// agility
												// course
												{
													owner.setAgilityCourseDef(course);
													owner.setCurrentCourseObject(def
															.getOrder());
												}
											}

											owner.getActionSender()
													.sendStat(16);
											stop();
										}
									}
								});
					}*/
				});
	}
}
