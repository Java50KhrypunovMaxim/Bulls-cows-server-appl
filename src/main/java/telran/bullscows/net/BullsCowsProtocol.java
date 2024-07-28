package telran.bullscows.net;
import telran.bulls.cows.net.*;
import java.util.List;
import java.util.Map;

import telran.bulls.net.BullsCowsMapImpl;
import telran.bulls.net.BullsCowsService;
import telran.bulls.net.Move;
import telran.bulls.net.MoveResult;
import telran.net.Protocol;
import telran.net.Request;
import telran.net.Response;
import telran.net.ResponseCode;

public class BullsCowsProtocol implements Protocol {
    private final BullsCowsService bullsCowsService = new BullsCowsMapImpl();

    @Override
    public Response getResponse(Request request) {
        String requestType = request.requestType();
        String requestData = request.requestData();
        Response response = null;
        try {
            response = switch (requestType) {
                case "create" -> createGame();
                case "move" -> processMove(requestData);
                case "isGameOver" -> checkGameOver(requestData);
                default -> wrongTypeResponse(requestType);
            };
        } catch (Exception e) {
            response = wrongDataResponse(e.getMessage());
        }
        return response;
    }

    private Response wrongDataResponse(String message) {
        return new Response(ResponseCode.WRONG_REQUEST_DATA, message);
    }

    private Response wrongTypeResponse(String requestType) {
        return new Response(ResponseCode.WRONG_REQUEST_TYPE, requestType);
    }

    private Response createGame() {
        long gameId = bullsCowsService.createNewGame();
        return new Response(ResponseCode.OK, String.valueOf(gameId));
    }

    private Response processMove(String requestData) {
        MoveRequestData moveRequestData = parseMoveRequestData(requestData);
        long gameId = moveRequestData.getGameId();
        String guess = moveRequestData.getGuess();
        Move move = new Move(Long.toString(gameId), guess);
        List<MoveResult> results = bullsCowsService.getResults(gameId, move);
        if (results != null) {
            return new Response(ResponseCode.OK, moveResultsToString(results));
        } else {
            return new Response(ResponseCode.OK, "Game over, you have exceeded the maximum number of attempts.");
        }
    }

    private Response checkGameOver(String requestData) {
        long gameId = Long.parseLong(requestData);
        boolean isGameOver = bullsCowsService.isGameOver(gameId);
        return new Response(ResponseCode.OK, String.valueOf(isGameOver));
    }

    private MoveRequestData parseMoveRequestData(String requestData) {
        String[] parts = requestData.split(";");
        long gameId = Long.parseLong(parts[0]);
        String guess = parts[1];
        return new MoveRequestData(gameId, guess);
    }
    private static class MoveRequestData {
        private final long gameId;
        private final String guess;

        public MoveRequestData(long gameId, String guess) {
            this.gameId = gameId;
            this.guess = guess;
        }

        public long getGameId() {
            return gameId;
        }

        public String getGuess() {
            return guess;
        }
    }
    private String moveResultsToString(List<MoveResult> results) {
        StringBuilder sb = new StringBuilder();
        for (MoveResult result : results) {
            sb.append("Guess: ")
              .append(result.guessStr())
              .append(", Bulls: ")
              .append(result.bullsCows()[0])
              .append(", Cows: ")
              .append(result.bullsCows()[1])
              .append("\n");
        }
        return sb.toString();
    }
}