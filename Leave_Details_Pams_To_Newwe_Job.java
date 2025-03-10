package entrar_badge;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Leave_Details_Pams_To_Newwe_Job {

	private static final String SOURCE_DB_URL = "jdbc:postgresql://192.168.2.59:5432/pamsdb_test_17JAN2024";
	private static final String DESTINATION_DB_URL = "jdbc:postgresql://localhost:5432/qa2_demo_29April2024";
	private static final String SOURCE_DB_USER = "postgres";
	private static final String SOURCE_DB_PASSWORD = "postgress";
	private static final String DESTINATION_DB_USER = "postgres";
	private static final String DESTINATION_DB_PASSWORD = "postgres";

	public static void main(String[] args) {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("PostgreSQL JDBC driver not found. Include it in your project.");
			return;
		}

		transferLeaveDetails();
	}

	public static void transferLeaveDetails() {

		try (Connection sourceConn = DriverManager.getConnection(SOURCE_DB_URL, SOURCE_DB_USER, SOURCE_DB_PASSWORD);
				Connection targetConn = DriverManager.getConnection(DESTINATION_DB_URL, DESTINATION_DB_USER,
						DESTINATION_DB_PASSWORD)) {

			Map<String, String> employeeMap = new HashMap<>();
			String selectQuery = "SELECT emp_code, id FROM public.emp WHERE active = true";
			try (PreparedStatement selectStmt = targetConn.prepareStatement(selectQuery);
					ResultSet rs = selectStmt.executeQuery()) {
				while (rs.next()) {
					String empCode = rs.getString("emp_code");
					String empId = rs.getString("id");
					employeeMap.put(empCode, empId);
				}
			}

			Set<Integer> leaveIds = new HashSet<>();
			String Query = "SELECT leave_id FROM public.emp_leave_request_details";
			try (PreparedStatement selectStmt = targetConn.prepareStatement(Query);
					ResultSet rs = selectStmt.executeQuery()) {
				while (rs.next()) {
					int leaveId = rs.getInt("leave_id");
					leaveIds.add(leaveId);
				}
			}

			String leaveIdsString = leaveIds.stream().map(String::valueOf).collect(Collectors.joining(","));
			LocalDate today = LocalDate.now();
			LocalDate tenDaysAgo = today.minusDays(10);
			String sourceselectQuery;
			if (leaveIds.isEmpty()) {
				sourceselectQuery = "SELECT id, emp_id, from_date, to_date, time_period, updated_date, is_cancelled FROM public.leave_request ";
			} else {
				sourceselectQuery = "SELECT id, emp_id, from_date, to_date, time_period, updated_date, is_cancelled "
						+ "FROM public.leave_request " + "WHERE id NOT IN (" + leaveIdsString + ") "
						+ "or  DATE(updated_date) >= '" + tenDaysAgo + "'";
			}

			try (PreparedStatement selectStmt = sourceConn.prepareStatement(sourceselectQuery);
					ResultSet rs = selectStmt.executeQuery()) {

				while (rs.next()) {

					String emp_id = null;
					if (employeeMap.containsKey(rs.getString("emp_id"))) {
						emp_id = employeeMap.get(rs.getString("emp_id"));

						if (rs.getBoolean("is_cancelled")) {
							deleteLeaveRecord(targetConn, rs.getInt("id"));
						} else {

							if (leaveIds.contains(rs.getInt("id"))) {

								if (isUpdatedToday(
										LocalDate.ofInstant(rs.getTimestamp("updated_date").toInstant(), java.time.ZoneId.systemDefault())
										)) {
									updateLeaveRecord(targetConn, rs.getInt("id"), emp_id, rs.getString("emp_id"),
											rs.getDate("from_date"), rs.getDate("to_date"),
											rs.getObject("time_period", Integer.class));
								}
							} else {
								insertLeaveRecord(targetConn, rs.getInt("id"), emp_id, rs.getString("emp_id"),
										rs.getDate("from_date"), rs.getDate("to_date"),
										rs.getObject("time_period", Integer.class));
							}

						}
					}

				}
			}

			System.out.println("Leave details transferred successfully.");

		} catch (SQLException e) {
			System.err.println("Error transferring leave details: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void insertLeaveRecord(Connection targetConn, int leave_id, String emp_id, String emp_code,
			Date from_date, Date to_date, Integer time_period) throws SQLException {
		String insertQuery = "INSERT INTO  public	.emp_leave_request_details (leave_id, emp_id, emp_code, from_date, to_date, time_period) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement insertStmt = targetConn.prepareStatement(insertQuery)) {
			insertStmt.setInt(1, leave_id);
			insertStmt.setString(2, emp_id);
			insertStmt.setString(3, emp_code);
			insertStmt.setDate(4, from_date);
			insertStmt.setDate(5, to_date);
			insertStmt.setObject(6, time_period);
			insertStmt.executeUpdate();
		}
	}

	private static void updateLeaveRecord(Connection targetConn, int leave_id, String emp_id, String emp_code,
			Date from_date, Date to_date, Integer time_period) throws SQLException {
		String updateQuery = "UPDATE public.emp_leave_request_details SET emp_id = ?, emp_code = ?, from_date = ?, to_date = ?, time_period = ? WHERE leave_id = ?";
		try (PreparedStatement updateStmt = targetConn.prepareStatement(updateQuery)) {
			updateStmt.setString(1, emp_id);
			updateStmt.setString(2, emp_code);
			updateStmt.setDate(3, from_date);
			updateStmt.setDate(4, to_date);
			updateStmt.setObject(5, time_period);
			updateStmt.setInt(6, leave_id);
			updateStmt.executeUpdate();
		}
	}

	private static void deleteLeaveRecord(Connection targetConn, int leave_id) throws SQLException {
		String deleteQuery = "DELETE FROM public.emp_leave_request_details WHERE leave_id = ?";
		try (PreparedStatement deleteStmt = targetConn.prepareStatement(deleteQuery)) {
			deleteStmt.setInt(1, leave_id);
			deleteStmt.executeUpdate();
		}
	}

	private static boolean isUpdatedToday(LocalDate date) {
		if (date == null) {
			return false;
		}
		LocalDate currentDate = LocalDate.now();
	    LocalDate tenDaysAgo = currentDate.minusDays(10);
	    return !date.isBefore(tenDaysAgo);
	}

}
