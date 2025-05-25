package cn.chengzhiya.mhdfverify.console;

import cn.chengzhiya.mhdfverify.Main;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedString;

import java.util.List;

public final class CommandCompleter implements Completer {
    @Override
    public void complete(LineReader reader, final ParsedLine commandLine, final List<Candidate> candidates) {
        assert commandLine != null;
        assert candidates != null;

        for (String string : Main.getCommandManager().tabComplete(commandLine.line())) {
            candidates.add(new Candidate(AttributedString.stripAnsi(string), string, null, null, null, null, true));
        }
    }
}
