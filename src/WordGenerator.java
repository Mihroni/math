import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Random;

public class WordGenerator {

    static String maskWord(String word) {
        char[] letters = word.toCharArray();
        StringBuilder wordToReturn = new StringBuilder();
        for (int i = 0; i < letters.length; i++) {
            if (i != 0 && i != letters.length - 1) {
                letters[i] = '*';
            }
            wordToReturn.append(letters[i]);
        }
        return wordToReturn.toString();
    }

    public static String guess(String word, String guess, String maskedWord) {
        boolean changedValue = false;
        boolean winner = true;
        char[] letters = word.toCharArray();
        char[] guessLetter = guess.toCharArray();
        char[] arrayToReturn = maskedWord.toCharArray();
        StringBuilder result = new StringBuilder();
        if (guess.length() < 1) {
            return "Invalid guess";
        } else {
            for (int i = 0; i <letters.length ; i++) {
                if (letters[i] == guessLetter[0]){
                    arrayToReturn[i] = guessLetter[0];
                }
            }
        }
        for (int i = 0; i <arrayToReturn.length ; i++) {
            result.append(arrayToReturn[i]);
            if (arrayToReturn[i] == '*'){
                winner = false;
            }
        }
        if (winner){
            return "You win";
        }
        return result.toString();
    }

    public static String generateProblem(){
        Random rand = new Random();
        int num1 = rand.nextInt(5) + 1;
        int num2 = rand.nextInt(5) + 1;
        int answer = num1+num2;
        String toSave = Integer.toString(answer);
        String data = num1 + " + " + num2;
        try {
            FileWriter writer = new FileWriter("word.txt", true);
            writer.write(toSave);
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
    public static void deleteFileContent(String fileName) {
        File file = new File(fileName);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write("");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
