declare module 'annyang' {
    interface CommandObject {
      [command: string]: () => void;
    }
  
    function addCommands(commands: CommandObject): void;
    function start(): void;
    function abort(): void;
    function addCallback(event: string, callback: (phrases: string[]) => void): void;
    function removeCommands(): void;
    function setLanguage(lang: string) : void;
    export = annyang;
  }