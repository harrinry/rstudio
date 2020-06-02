/*
 * AppCommandPaletteSource.java
 *
 * Copyright (C) 2020 by RStudio, PBC
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */
package org.rstudio.studio.client.palette;

import java.util.ArrayList;
import java.util.List;

import org.rstudio.core.client.StringUtil;
import org.rstudio.core.client.command.AppCommand;
import org.rstudio.core.client.command.KeyMap;
import org.rstudio.core.client.command.KeyMap.KeyMapType;
import org.rstudio.core.client.command.KeySequence;
import org.rstudio.core.client.command.ShortcutManager;
import org.rstudio.studio.client.palette.model.CommandPaletteEntrySource;
import org.rstudio.studio.client.palette.ui.AppCommandPaletteEntry;
import org.rstudio.studio.client.palette.ui.CommandPaletteEntry;
import org.rstudio.studio.client.workbench.commands.Commands;

public class AppCommandPaletteSource implements CommandPaletteEntrySource<AppCommand>
{
   public AppCommandPaletteSource(ShortcutManager shortcuts, Commands commands)
   {
      commands_ = commands;
      map_ = shortcuts.getKeyMap(KeyMapType.APPLICATION);
   }

   @Override
   public List<AppCommand> getPaletteCommands()
   {
      ArrayList<AppCommand> commands = new ArrayList<AppCommand>();
      commands.addAll(commands_.getCommands().values());
      return commands;
   }

   @Override
   public CommandPaletteEntry renderPaletteCommand(AppCommand command)
   {
      String id = command.getId();
      if (id.contains("Mru") || id.startsWith("mru") || id.contains("Dummy"))
      {
         // MRU entries and dummy commands should not appear in the palette
         return null;
      }
      
      // Ensure the command is visible. It'd be nice to show all commands in
      // the palette for the purposes of examining key bindings, discovery,
      // etc., but invisible commands are generally meaningless in the 
      // current context.
      if (!command.isVisible())
      {
         return null;
      }

      // Look up the key binding for this command
      List<KeySequence> keys = map_.getBindings(command.getId());
      
      // Create an application command entry
      CommandPaletteEntry entry = new AppCommandPaletteEntry(command, keys);
      if (StringUtil.isNullOrEmpty(entry.getLabel()))
      {
         // Ignore app commands which have no label
         return null;
      }

      return entry;
   }

   private final KeyMap map_;
   private final Commands commands_;
}
